package com.wsh.fingerwords.protocol;

import java.io.IOException;

import frame.protocol.BaseHttpResponse;
import frame.protocol.BaseXMLRequest;
import frame.util.XmlSerializer;


public class DictRequest extends BaseXMLRequest {
	
	String word="";
	
	public DictRequest(String word){
		this.word=word;
		setAbsoluteURI("http://word.iyuba.com/words/apiWord.jsp?q="+word);
	}
	
	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new DictResponse();
	}

}
