/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Common Public License 1.0 (http://opensource.org/licenses/cpl.php)
 *   which can be found in the file CPL.TXT at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Jul 31, 2008 */

package clojure.lang;

import java.util.concurrent.ConcurrentMap;
import java.util.*;

public class TransactionalHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>{
final Ref[] bins;

IPersistentMap mapAt(int bin){
	return (IPersistentMap) bins[bin].get();
}

final int binFor(Object k){
	//spread hashes, a la Cliff Click
	int h = k.hashCode();
	h ^= (h >>> 20) ^ (h >>> 12);
	h ^= (h >>> 7) ^ (h >>> 4);
	return h % bins.length;
//	return k.hashCode() % bins.length;
}

Entry entryAt(Object k){
	return mapAt(binFor(k)).entryAt(k);
}

public TransactionalHashMap() throws Exception{
	this(421);
}

public TransactionalHashMap(int nBins) throws Exception{
	bins = new Ref[nBins];
	for(int i = 0; i < nBins; i++)
		bins[i] = new Ref(PersistentHashMap.EMPTY);
}

public TransactionalHashMap(Map<? extends K, ? extends V> m) throws Exception{
	this(m.size());
	putAll(m);
}

public int size(){
	int n = 0;
	for(int i = 0; i < bins.length; i++)
		{
		n += mapAt(i).count();
		}
	return n;
}

public boolean isEmpty(){
	return size() == 0;
}

public boolean containsKey(Object k){
	return entryAt(k) != null;
}

public V get(Object k){
	Entry e = entryAt(k);
	if(e != null)
		return (V) e.getValue();
	return null;
}

public V put(K k, V v){
	Ref r = bins[binFor(k)];
	IPersistentMap map = (IPersistentMap) r.get();
	Object ret = map.valAt(k);
	r.set(map.assoc(k, v));
	return (V) ret;
}

public V remove(Object k){
	Ref r = bins[binFor(k)];
	IPersistentMap map = (IPersistentMap) r.get();
	Object ret = map.valAt(k);
	//checked exceptions are a bad idea, especially in an interface
	try
		{
		r.set(map.without(k));
		}
	catch(Exception e)
		{
		throw new RuntimeException(e);
		}
	return (V) ret;
}

public void putAll(Map<? extends K, ? extends V> map){
	for(Iterator i = map.entrySet().iterator(); i.hasNext();)
		{
		Entry<K, V> e = (Entry) i.next();
		put(e.getKey(), e.getValue());
		}
}

public void clear(){
	for(int i = 0; i < bins.length; i++)
		{
		Ref r = bins[i];
		IPersistentMap map = (IPersistentMap) r.get();
		if(map.count() > 0)
			{
			r.set(PersistentHashMap.EMPTY);
			}
		}
}

public Set<Entry<K, V>> entrySet(){
	final ArrayList<Map.Entry<K, V>> entries = new ArrayList(bins.length);
	for(int i = 0; i < bins.length; i++)
		{
		IPersistentMap map = mapAt(i);
		if(map.count() > 0)
			entries.addAll((Collection) RT.seq(map));
		}
	return new AbstractSet<Entry<K, V>>(){
		public Iterator iterator(){
			return Collections.unmodifiableList(entries).iterator();
		}

		public int size(){
			return entries.size();
		}
	};
}

public V putIfAbsent(K k, V v){
	Ref r = bins[binFor(k)];
	IPersistentMap map = (IPersistentMap) r.get();
	Entry e = map.entryAt(k);
	if(e == null)
		{
		r.set(map.assoc(k, v));
		return null;
		}
	else
		return (V) e.getValue();
}

public boolean remove(Object k, Object v){
	Ref r = bins[binFor(k)];
	IPersistentMap map = (IPersistentMap) r.get();
	Entry e = map.entryAt(k);
	if(e != null && e.getValue().equals(v))
		{
		//checked exceptions are a bad idea, especially in an interface
		try
			{
			r.set(map.without(k));
			}
		catch(Exception ex)
			{
			throw new RuntimeException(ex);
			}
		return true;
		}
	return false;
}

public boolean replace(K k, V oldv, V newv){
	Ref r = bins[binFor(k)];
	IPersistentMap map = (IPersistentMap) r.get();
	Entry e = map.entryAt(k);
	if(e != null && e.getValue().equals(oldv))
		{
		r.set(map.assoc(k, newv));
		return true;
		}
	return false;
}

public V replace(K k, V v){
	Ref r = bins[binFor(k)];
	IPersistentMap map = (IPersistentMap) r.get();
	Entry e = map.entryAt(k);
	if(e != null)
		{
		r.set(map.assoc(k, v));
		return (V) e.getValue();
		}
	return null;
}

}
