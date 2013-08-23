package com.cantwellcode.cantwellgallery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Chris on 8/23/13.
 * Implementation of List that supports managed modification and observer notification of modified contents.
 */
public class ManagedList<E> implements List<E> {
    private static final String NULL_MANAGER            = "Argument manager may not be null.";
    private static final String MANAGER_NOT_REGISTERED  = "Only registered managers may modify data.";
    public interface Manager{}
    public interface Observer{
        public void onNotify();
    }

    private ArrayList<E> mData;
    private ArrayList<Manager> mManagers;
    private ArrayList<Observer> mObservers;

    public ManagedList(){
        init(new ArrayList<E>(), new ArrayList<Manager>(), new ArrayList<Observer>());
    }
    public ManagedList(int capacity){
        init(new ArrayList<E>(capacity), new ArrayList<Manager>(), new ArrayList<Observer>());
    }
    public ManagedList(Collection<? extends E> collection){
        init(new ArrayList<E>(collection), new ArrayList<Manager>(), new ArrayList<Observer>());
    }
    public ManagedList(int capacity, Collection<? extends Manager> managers, Collection<? extends Observer> observers){
        init(new ArrayList<E>(capacity), new ArrayList<Manager>(managers), new ArrayList<Observer>(observers));
    }
    public ManagedList(Collection<? extends E> collection, Collection<? extends Manager> managers, Collection<? extends Observer> observers){
        init(new ArrayList<E>(collection), new ArrayList<Manager>(managers), new ArrayList<Observer>(observers));
    }
    private void init(ArrayList<E> data, ArrayList<Manager> managers, ArrayList<Observer> observers){
        mData = data;
        mManagers = managers;
        mObservers = observers;
    }
    private boolean verifyManager(Manager manager){
        if (null == manager) throw new NullPointerException(NULL_MANAGER);
        if (mManagers.contains(manager)) return true;
        else throw new UnsupportedOperationException(MANAGER_NOT_REGISTERED);
    }
    private void notifyObservers(){
        for(Observer observer : mObservers) observer.onNotify();
    }

    /**
     * Registers an object that implements ManagedList.Manager.
     * Only registered ManagedList.Manager objects may modify data stored in the List.
     * @param manager
     */
    public void registerManager(Manager manager){
        if ( !mManagers.contains(manager) ) mManagers.add(manager);
    }

    /**
     * Registers an Observer for the ManagedList.
     * Registered Observers are notified when data in the List has been modified
     * @param observer
     */
    public void registerObserver(Observer observer){
        if ( !mObservers.contains(observer) ) mObservers.add(observer);
    }

    /**
     * Unregisters a Manager
     * @param manager
     * @return true if the Manager was registered and now isn't, false otherwise.
     */
    public boolean unregisterManager(Manager manager){
        return mManagers.remove(manager);
    }

    /**
     * Unregisters an Observer
     * @param observer
     * @return true if the Observer was registered and now isn't, false otherwise.
     */
    public boolean unregisterObserver(Observer observer){
        return mObservers.remove(observer);
    }

    /**
     * Not supported. Use: add(Manager manager, int index, E e)
     * @param i
     * @param e
     */
    @Override
    public void add(int i, E e) {
        final String MESSAGE = "Use: add(Manager manager, int index, E e)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Insert element into list
     * @param manager : instance of Manager making the add request
     * @param i : location of insertion
     * @param e : element to be added
     */
    public void add(Manager manager, int i, E e){
        if (verifyManager(manager))mData.add(i,e);
        notifyObservers();
    }

    /**
     * Not supported. Use: add(Manager manager, E e)
     * @param e
     * @return
     */
    @Override
    public boolean add(E e) {
        final String MESSAGE = "Use: add(Manager manager, E e)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Add element to end of list
     * @param manager : instance of Manager making the add request
     * @param e : element to be added
     * @return
     */
    public boolean add(Manager manager, E e){
        if (verifyManager(manager)){
            if(mData.add(e)){
                notifyObservers();
                return true;
            }
        }
        return false;
    }

    /**
     * Not supported. Use: addAll(Manager manager, int i, Colection<? extends E> es)
     * @param i
     * @param es
     * @return
     */
    @Override
    public boolean addAll(int i, Collection<? extends E> es) {
        final String MESSAGE = "Use: addAll(Manager manager, int i, Colection<? extends E> es)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Insert collection into list
     * @param manager : instance of Manager making the add request
     * @param i : location of insertion
     * @param es : collection being added
     * @return
     */
    public boolean addAll(Manager manager, int i, Collection<? extends E> es){
        if (verifyManager(manager)){
            if(mData.addAll(i,es)){
                notifyObservers();
                return true;
            }
        }
        return false;
    }

    /**
     * Not supported. Use: addAll(Manager manager, Colection<? extends E> es)
     * @param es
     * @return
     */
    @Override
    public boolean addAll(Collection<? extends E> es) {
        final String MESSAGE = "Use: addAll(Manager manager, Colection<? extends E> es)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Add collection to end of list
     * @param manager : instance of Manager making the add request
     * @param es : collection being added
     * @return
     */
    public boolean addAll(Manager manager, Collection<? extends E> es){
        if (verifyManager(manager)){
            if (mData.addAll(es)){
                notifyObservers();
                return true;
            }
        }
        return false;
    }

    /**
     * Not supported.  Use: clear(Manager manager)
     */
    @Override
    public void clear() {
        final String MESSAGE = "Use: clear(Manager manager)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Clear list
     * @param manager : instance of Manager making the clear request
     */
    public void clear(Manager manager){
        if (verifyManager(manager)){
            mData.clear();
            notifyObservers();
        }
    }

    @Override
    public boolean contains(Object o) {
        return mData.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return mData.containsAll(objects);
    }

    @Override
    public E get(int i) {
        return mData.get(i);
    }

    @Override
    public int indexOf(Object o) {
        return mData.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return mData.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return mData.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return mData.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        return mData.listIterator(i);
    }

    /**
     * Not supported. Use: remove(Manager manager, int i)
     * @param i
     * @return
     */
    @Override
    public E remove(int i) {
        final String MESSAGE = "Use: remove(Manager manager, int i)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Remove element from list
     * @param manager : instance of Manager making the request
     * @param i : location of element
     * @return the element removed
     */
    public E remove(Manager manager, int i){
        if (verifyManager(manager)){
            E e = mData.remove(i);
            notifyObservers();
            return e;
        }
        return null;
    }

    /**
     * Not supported. Use: remove(Manager manager, Object o)
     * @param o
     * @return
     */
    @Override
    public boolean remove(Object o) {
        final String MESSAGE = "Use: remove(Manager manager, Object o)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Remove object from list
     * @param manager : instance of Manager making the request
     * @param o : object to be removed
     * @return : true on successful removal, false otherwise
     */
    public boolean remove(Manager manager, Object o){
        if (verifyManager(manager)){
            if (mData.remove(o)){
                notifyObservers();
                return true;
            }
        }
        return false;
    }

    /**
     * Not supported. Use: removeAll(Manager manager, Collection<?> objects)
     * @param objects
     * @return
     */
    @Override
    public boolean removeAll(Collection<?> objects) {
        final String MESSAGE = "Use: removeAll(Manager manager, Collection<?> objects)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Remove a collection of objects from list
     * @param manager : instance of Manager making the request
     * @param objects : collection of objects to be removed.
     * @return : true on succesful removal, false otherwise.
     */
    public boolean removeAll(Manager manager, Collection<?> objects){
        if (verifyManager(manager)){
            if(mData.removeAll(objects)){
                notifyObservers();
                return true;
            }
        }
        return false;
    }

    /**
     * Not supported.  Use: retainAll(Manager manager, Collection<?> objects)
     * @param objects
     * @return
     */
    @Override
    public boolean retainAll(Collection<?> objects) {
        final String MESSAGE = "Use: retainAll(Manager manager, Collection<?> objects)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Retains a collection of objects in the list.  Removes all objects not in the retained collection
     * @param manager : instance of Manager making the request
     * @param objects : collection of objects to be retained.
     * @return : true on successful retain, false otherwise.
     */
    public boolean retainAll(Manager manager, Collection<?> objects){
        if (verifyManager(manager)){
            if(mData.retainAll(objects)){
                notifyObservers();
                return true;
            }
        }
        return false;
    }

    /**
     * Not supported. Use: set(Manager manager, int i, E e)
     * @param i
     * @param e
     * @return
     */
    @Override
    public E set(int i, E e) {
        final String MESSAGE = "Use: set(Manager manager, int i, E e)";
        throw new UnsupportedOperationException(MESSAGE);
    }

    /**
     * Set element
     * @param manager : instance of Manager making the request
     * @param i : location of element
     * @param e : element value to be set
     * @return old element
     */
    public E set(Manager manager, int i, E e){
        if (verifyManager(manager)){
            E old = mData.set(i,e);
            notifyObservers();
            return old;
        }
        return null;
    }

    /**
     * Get the number of elements in the list
     * @return
     */
    @Override
    public int size() {
        return mData.size();
    }

    /**
     * Get a subset of the data stored in list between i and i2
     * This does not necessarily return a ManagedList.
     * Data is not guaranteed to reflect changes made by the manager.
     * The list may not be observable
     * @param i
     * @param i2
     * @return
     */
    @Override
    public List<E> subList(int i, int i2) {
        return mData.subList(i,i2);
    }

    /**
     * Get an array representation of elements in the list.
     * This does not return a managed data set.
     * @return
     */
    @Override
    public Object[] toArray() {
        return mData.toArray();
    }

    /**
     * Get a typed array representation of elements in the list
     * Not managed
     * @param ts
     * @param <T>
     * @return
     */
    @Override
    public <T> T[] toArray(T[] ts) {
        return (T[]) mData.toArray(ts);
    }

}
