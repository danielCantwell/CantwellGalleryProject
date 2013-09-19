package com.cantwellcode.cantwellgallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Daniel on 9/15/13.
 */
public class SwipeListViewDetect  implements View.OnTouchListener {

    // Constants. Not declared "final" because they must be assigned later
    private int  mSlop;
    private int  mMinFlingVelocity;
    private int  mMaxFlingVelocity;
    private long mAnimationTime;

    // Fixed variables
    private ListView mListView;
    private SlideCallbacks mCallbacks;
    private int mViewWidth = 1;

    // Other variables
    private List<PendingSlideData> mPendingSlides = new ArrayList<PendingSlideData>();
    private int         mSlideAnimationRefCount = 0;
    private VelocityTracker mVelocityTracker;
    private float       mDownX;
    private int         mDownPosition;
    private View        mDownView;
    private boolean     mSwiping;
    private boolean     mPaused;

    public enum Direction {Left, Right};

    /**
     * Callback interface used to inform its client of a slide
     */
    public interface SlideCallbacks {

        /**
         * Called when the user has slid a view
         * @param listView               - the view to slide
         * @param reverseSortedPositions - an array of positions to slide
         */
        void onSlide(ListView listView, int[] reverseSortedPositions, Direction swipeDirection);
    }

    public SwipeListViewDetect(ListView listView, SlideCallbacks callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity   = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity   = vc.getScaledMaximumFlingVelocity();
        mAnimationTime      = listView.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        mListView   = listView;
        mCallbacks  = callbacks;
    }

    /**
     * Pauses or resumes watching for swipe gestures.
     *
     * @param enabled - whether or not to watch for gestures.
     */
    public void setEnabled(boolean enabled) {
        mPaused = !enabled;
    }

    public AbsListView.OnScrollListener ScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                setEnabled(scrollState != SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                // nothing
            }
        };
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mViewWidth < 2) {
            mViewWidth = mListView.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (mPaused) {
                    return false;
                }

                // TODO: ensure this is a finger, and set a flag

                // Find the child view that was touched (perform a hit test)
                Rect rect = new Rect();
                int childCount = mListView.getChildCount();
                int[] listViewCoords = new int[2];
                mListView.getLocationOnScreen(listViewCoords);
                int x = (int) motionEvent.getRawX() - listViewCoords[0];
                int y = (int) motionEvent.getRawY() - listViewCoords[1];
                View child;

                for (int i = 0; i < childCount; i++) {
                    child = mListView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        mDownView = child;
                        break;
                    }
                }

                if (mDownView != null) {
                    mDownX = motionEvent.getRawX();
                    mDownPosition = mListView.getPositionForView(mDownView);

                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(motionEvent);
                }
                return false;
            }

            case MotionEvent.ACTION_UP: {
                if (mVelocityTracker == null) {
                    break;
                }

                float deltaX = motionEvent.getRawX() - mDownX;

                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);

                float velocityX     = mVelocityTracker.getXVelocity();
                float absVelocityX  = Math.abs(velocityX);
                float absVelocityY  = Math.abs(mVelocityTracker.getYVelocity());

                boolean slide = false;
                boolean slideRight = false;

                if (Math.abs(deltaX) > mViewWidth / 2) {
                    slide = true;
                    slideRight = deltaX > 0;
                } else if ((mMinFlingVelocity <= absVelocityX) && (absVelocityX <= mMaxFlingVelocity)
                        && (absVelocityY < absVelocityX)) {
                    // slide only if flinging in the same direction as dragging
                    slide = (velocityX < 0) == (deltaX < 0);
                    slideRight = mVelocityTracker.getXVelocity() > 0;

                }
                if (slide) {
                    final Direction swipeDirection;
                    if (slideRight) {
                        swipeDirection = Direction.Right;
                    } else {
                        swipeDirection = Direction.Left;
                    }
                    // slide
                    final View downView = mDownView; // mDownView gets null'd before animation ends
                    final int downPosition = mDownPosition;

                    ++mSlideAnimationRefCount;

                    mDownView.animate()
                            .translationX(slideRight ? mViewWidth : -mViewWidth)
                            .alpha(0)
                            .setDuration(mAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    performSlide(downView, downPosition, swipeDirection);
                                }
                            });
                } else {
                    // cancel
                    mDownView.animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownView = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mVelocityTracker == null || mPaused) {
                    break;
                }

                mVelocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - mDownX;
                if (Math.abs(deltaX) > mSlop) {
                    mSwiping = true;
                    mListView.requestDisallowInterceptTouchEvent(true);

                    // Cancel ListView's touch (un-highlight the item)
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex()
                                    << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mListView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (mSwiping) {
                    mDownView.setTranslationX(deltaX);
                    mDownView.setAlpha(Math.max(0f, Math.min(1f,
                            1f - 2f * Math.abs(deltaX) / mViewWidth)));
                    return true;
                }
                break;
            }
        }

        return false;
    }

    class PendingSlideData implements Comparable<PendingSlideData> {
        public int position;
        public View view;

        public PendingSlideData(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public int compareTo(PendingSlideData pendingSlideData) {
            return pendingSlideData.position - position;
        }
    }

    private void performSlide(final View slideView, final int slidePosition, final Direction direction) {
        // Animate teh slide list item to zero-height and fire the slide callback
        // when all slide list item animations have completed

        final ViewGroup.LayoutParams lp = slideView.getLayoutParams();
        final int originalHeight = slideView.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                --mSlideAnimationRefCount;
                if (mSlideAnimationRefCount == 0) {
                    // No active animations, process all pending dismisses.
                    // Sort by descending position
                    Collections.sort(mPendingSlides);

                    int[] slidePositions = new int[mPendingSlides.size()];
                    for (int i = mPendingSlides.size() - 1; i >= 0; i--) {
                        slidePositions[i] = mPendingSlides.get(i).position;
                    }
                    mCallbacks.onSlide(mListView, slidePositions, direction);

                    ViewGroup.LayoutParams lp;
                    for (PendingSlideData pendingSlide : mPendingSlides) {
                        // Reset view presentation
                        pendingSlide.view.setAlpha(1f);
                        pendingSlide.view.setTranslationX(0);
                        lp = pendingSlide.view.getLayoutParams();
                        lp.height = originalHeight;
                        pendingSlide.view.setLayoutParams(lp);
                    }

                    mPendingSlides.clear();
                }
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                slideView.setLayoutParams(lp);
            }
        });

        mPendingSlides.add(new PendingSlideData(slidePosition, slideView));
        animator.start();
    }
}
