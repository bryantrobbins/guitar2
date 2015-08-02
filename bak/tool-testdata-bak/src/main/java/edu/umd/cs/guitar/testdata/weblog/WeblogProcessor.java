package edu.umd.cs.guitar.testdata.weblog;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import com.mongodb.DB;
import edu.umd.cs.guitar.testdata.AbstractStringIterator;
import edu.umd.cs.guitar.testdata.loader.ArtifactProcessor;

public class WeblogProcessor implements ArtifactProcessor<Weblog> {

	public static String KEY = "weblog";

	private Gson gson = new Gson();;

	@Override
	public String process(String path) throws IOException {
		Weblog theLog = new Weblog(path);
		return gson.toJson(theLog);
	}

	@Override
	public Weblog objectify(String json) {
		return gson.fromJson(json, Weblog.class);
	}

	@Override
	public String getKey() {
		return KEY;
	}

    @Override
    public void setDB(DB db) {
        return;
    }

    @Override
	public Iterator<String> getIterator(List<Object> logs){
		return new WeblogIterator(logs);
	}
	
	private class WeblogIterator extends AbstractStringIterator {

		public WeblogIterator(List<Object> objs){
			super(objs);
		}
		
		@Override
		public String getStringForObj(Object obj) {
			Weblog log = (Weblog) obj;
			if(! (obj instanceof Weblog)){
				throw new RuntimeException("Object is not of correct type in WeblogIterator");
			}
			
			String ret = "";
			for (int i = 0; i < log.getLines().size(); i++) {
				ret += log.getLines().get(i);

				if (i != log.getLines().size() - 1) {
					ret += " ";
				}
			}

			return ret;
		}
	}
}
