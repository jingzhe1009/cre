package com.bonc.framework.api.observer;

public interface IRuleApiObserverable {
	
	public void registerObserver(IRuleApiObserver obj);
	public void removeObserver(IRuleApiObserver obj);
	public void notifyObserver(IRuleApiObserver obj);

}
