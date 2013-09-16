package com.cantwellcode.cantwellgallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by Daniel on 9/15/13.
 */
public class SwipeViewDetect implements View.OnTouchListener {

    // Constants. Not declared "final" because they must be assigned later
    private int  mSlop;
    private int  mMinFlingVelocity;
    private int  mMaxFlingVelocity;
    private long mAnimationTime;

    // Fixed variables
    private View mView;
    private SlideCallbacks mCallbacks;
    private int mViewWidth = 1;

    // Other variables
    private float           mDownX;
    private float           mTranslationX;
    private boolean         mSwiping;
    private Object          mToken;
    private VelocityTracker mVelocityTracker;


    /**
     * Callback interface used to inform its client of a slide
     */
    public interface SlideCallbacks {

        /**
         * Called when the user has slid a view
         * @param view  - the view to slide
         * @param token - the optional token
         */
        void onSlide(View view, Object token);
    }

    public SwipeViewDetect(View view, Object token, SlideCallbacks callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity   = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity   = vc.getScaledMaximumFlingVelocity();
        mAnimationTime      = view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        mView       = view;
        mToken      = token;
        mCallbacks  = callbacks;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // offset because the view is translated during swipe
        motionEvent.offsetLocation(mTranslationX, 0);

        if (mViewWidth < 2) {
            mViewWidth = mView.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                // TODO: ensure this is a finger, and set a flag
                mDownX = motionEvent.getRawX();
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(motionEvent);

                return false;
            }

            case MotionEvent.ACTION_UP: {
                if (mVelocityTracker == null) {
                    break;
                }

                float deltaX = motionEvent.getRawX() - mDownX;

                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());

                boolean slide       = false;
                boolean slideRight  = false;

                if (Math.abs(deltaX) > mViewWidth / 2) {
                    slide = true;
                    slideRight = deltaX > 0;
                } else if ((mMinFlingVelocity <= absVelocityX) && (absVelocityX <= mMaxFlingVelocity)
                        && (absVelocityY < absVelocityX)) {
                    // slide only if flinging in the same direction as dragging
                    slide       = (velocityX < 0) == (deltaX < 0);
                    slideRight  = mVelocityTracker.getXVelocity() > 0;
                }

                if (slide) {
                    // slide
                    mView.animate()
                            .translationX(slideRight ? mViewWidth : -mViewWidth)
                            .alpha(0)
                            .setDuration(mAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    performSlide();
                                }
                            });
                } else {
                    // cancel
                    mView.animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                }

                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mTranslationX = 0;
                mDownX = 0;
                mSwiping = false;

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mVelocityTracker == null) {
                    break;
                }

                mVelocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - mDownX;

                if (Math.abs(deltaX) > mSlop) {
                    mSwiping = true;
                    mView.getParent().requestDisallowInterceptTouchEvent(true);

                    // Cancel listview's touch
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex() << motionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (mSwiping) {
                    mTranslationX = deltaX;
                    mView.setTranslationX(deltaX);
                    // TODO: use an ease-out interpolator or such
                    mView.setAlpha(Math.max(0f, Math.min(1f,
                            1f - 2f * Math.abs(deltaX) / mViewWidth)));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private void performSlide() {
        // Animate the sliding view to zero-height and fire the slide callback

        final ViewGroup.LayoutParams lp = mView.getLayoutParams();
        final int originalHeight        = mView.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCallbacks.onSlide(mView, mToken);
                // Reset view presentation
                mView.setAlpha(1f);
                mView.setTranslationX(0);
                lp.height = originalHeight;
                mView.setLayoutParams(lp);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                mView.setLayoutParams(lp);
            }
        });

        animator.start();
    }
}
