package com.bonc.framework.api.observer;

import java.util.ArrayList;
import java.util.List;

public class RuleApiLogObserverable implements IRuleApiObserverable{
	private List<IRuleApiObserver> observers = new ArrayList<>();

	@Override
	public void registerObserver(IRuleApiObserver obj) {
		this.observers.add(obj);
	}

	@Override
	public void removeObserver(IRuleApiObserver obj) {
		int i = this.observers.indexOf(obj);
		if(i>=0){
			this.observers.remove(obj);
		}
	}

	@Override
	public void notifyObserver(IRuleApiObserver obj) {
		for(IRuleApiObserver o:observers){
			o.update("");
		}
		
	}

}
