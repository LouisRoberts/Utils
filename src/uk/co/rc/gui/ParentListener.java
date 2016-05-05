package uk.co.rc.gui;

/** interface to allow a child component to talk to the
 * parent
 */
public interface ParentListener
{
    /** An event has occureed for the parent to deal with
     * @param event - the event to deal with
     */
    public void eventOccured(String event);
}