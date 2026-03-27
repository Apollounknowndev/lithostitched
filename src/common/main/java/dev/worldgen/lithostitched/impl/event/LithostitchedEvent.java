package dev.worldgen.lithostitched.impl.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LithostitchedEvent<T> {
	protected Function<List<T>, T> invoker;
	protected List<T> listeners = new ArrayList<>();
	
	public LithostitchedEvent(Function<List<T>, T> invoker) {
		this.invoker = invoker;
	}
	
	public void register(T callback) {
		this.listeners.add(callback);
	}
	
	public T invoker() {
		return this.invoker.apply(this.listeners);
	}
}
