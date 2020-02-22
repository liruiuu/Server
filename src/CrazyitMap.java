import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CrazyitMap<K, V> extends HashMap<K,V> {

	public void removeByValue(Object value) {
		for (Object key : keySet()) {
			if (get(key) == value) {
				remove(key);
				break;
			}
		}
	}

	public Set<V> valueSet() {
		Set<V> result = new HashSet<V>();
		for (K key : keySet()) {
			result.add(get(key));
		}
		return result;
	}
	
	public K getKeyByValue(V val){
		for (K key :keySet())
		{
			if (get(key).equals(val)&&get(key)==val){
			return key;	
			}
		}
		return null;
	}
	public V put(K key,V value){
		for (V val:valueSet()){
			if (val.equals(value)&&val.hashCode()==value.hashCode()){
				throw new RuntimeException("MyMap实例中不允许有重复的value!");
			}
		}
		return (V) super.put(key, value);
	}
}
