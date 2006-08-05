/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Common Public License 1.0 (http://opensource.org/licenses/cpl.php)
 *   which can be found in the file CPL.TXT at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package clojure.lang;

public abstract class APersistentMap extends Obj implements IPersistentMap, Cloneable{

public Obj withMeta(IPersistentMap meta) {
    if(_meta == meta)
        return this;
    try{
    Obj ret = (Obj) clone();
    ret._meta = meta;
    return ret;
    }
    catch(CloneNotSupportedException ignore)
        {
        return null;
        }
}

public IPersistentCollection cons(Object o) {
    IMapEntry e = (IMapEntry)o;
    return assoc(e.key(), e.val());
}
}
