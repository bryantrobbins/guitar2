package edu.umd.cs.guitar.testdata;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class AbstractStringIterator implements Iterator<String> {

	private Logger iterLogger = LogManager
			.getLogger(AbstractStringIterator.class);

	private List<Object> objs;
	private int currentObj;

	public AbstractStringIterator(List<Object> objs) {
		this.objs = objs;
		this.currentObj = -1;
	}

	@Override
	public boolean hasNext() {
		return objs.size() > (currentObj + 1);
	}

	@Override
	public String next() {
		if (hasNext()) {
			currentObj++;
			iterLogger.debug("Log " + currentObj);
			return getStringForObj(objs.get(currentObj));
		}

		throw new NoSuchElementException(
				"No more elements in WeblogIterator");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"Remove operation not supported in WeblogIterator");
	}
	
	public abstract String getStringForObj(Object o);

}
