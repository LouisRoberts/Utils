package org.lorob.utils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

/**
 * Ordered HashMap. Remebers order that entries were added
 * @author lorob
 *
 */
public class OrderedHashMap extends HashMap
{
	private static final long serialVersionUID = 4262818494680282836L;
	private Vector _keys;
	    
    /**
     * Construct an OrderedHashMap 
     * @see java.util.Hashtable
    **/
    public OrderedHashMap(int initialCapacity, float loadFactor) 
    {
        super( initialCapacity, loadFactor );
        _keys = new Vector( initialCapacity , initialCapacity );
    }

    /**
     * Construct an OrderedHashMap 
     * @see java.util.Hashtable
    **/
    public OrderedHashMap(int initialCapacity) 
    {
        super( initialCapacity );
        _keys = new Vector( initialCapacity, initialCapacity );
    }

    /**
     * Construct an OrderedHashtable 
     * @see java.util.Hashtable
    **/
    public OrderedHashMap()
    {
        super();
        _keys = new Vector( 10, 10 );
    }

    // Overriden Hashtable methods

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    public synchronized void clear()
    {
        super.clear();
        _keys.removeAllElements();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public synchronized Object clone()
    {
    	OrderedHashMap ht = (OrderedHashMap)super.clone();
        ht._keys = (Vector)_keys.clone();
        return ht;
    }

    /* (non-Javadoc)
     * @see java.util.Dictionary#elements()
     */
    public synchronized Enumeration elements()
    {
        return new Enum(keys());
    }
    
    /**
     * @author lorob
     * Enumeration for ordered hash.
     */
    class Enum implements Enumeration
    {
        private final Enumeration _enum;
        public Enum( Enumeration e ){ _enum = e; }
        public synchronized boolean hasMoreElements(){ return _enum.hasMoreElements(); }
        public synchronized Object nextElement(){ return get( _enum.nextElement() ); }
    }

    /**
     * Return enumeration of keys
     */
    public synchronized Enumeration keys()
    {
        return _keys.elements();
    }

    /**
     * return object replaced by key
     */
    public synchronized Object put( Object key, Object value )
    {
        if (_keys != null && // null during serialization construction
            !_keys.contains( key ))
        {
            _keys.addElement( key );
        }
        return super.put( key, value );
    }

    /**
     * Remove handed object
     */
    public synchronized Object remove( Object key )
    {
        _keys.removeElement( key );
        return super.remove( key );
        
    }

    /**
     * Return a colletion of keys
     */
    public synchronized Collection  values()
    {
        return (Collection)_keys;
    }
}
