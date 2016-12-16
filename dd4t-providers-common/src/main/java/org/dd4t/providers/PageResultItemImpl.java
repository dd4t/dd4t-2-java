package org.dd4t.providers;

public class PageResultItemImpl extends StringResultItemImpl implements PageProviderResultItem<String>{

	private String url;
	public PageResultItemImpl(int publicationId, int itemId, String url) {
		super(publicationId, itemId);
		
		this.url = url;
	}
	
	@Override
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
