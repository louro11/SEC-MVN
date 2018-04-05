package hds;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.security.cert.Certificate;


public class Nonce {

	private static Nonce firstInstance = null;
	private ArrayList<Long> nonceSentList = new ArrayList<Long>();
	private Map<Long,String> nonceReceivedHash = new HashMap<Long, String>();
	private int count = 0;

	
	private Nonce(){}
	
	public static Nonce getInstance(){
		if(firstInstance == null)
			firstInstance = new Nonce();
		return firstInstance;
	}
	
	public boolean checkinboundNonce(String entity, long inboundNonce){
		System.out.println("  • Checking pair entity-nonce: " + entity +" - "+ inboundNonce);
		boolean found = false;
		if(nonceReceivedHash.get(inboundNonce) != null)
			found = true;	
		
		Iterator it = nonceReceivedHash.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
    		System.out.println("  • Comparing: "+pair.getKey() + " - "+ pair.getValue() + " with "+ entity +" - "+ inboundNonce);

	        if((((long) pair.getKey()) == inboundNonce) && ((String) pair.getValue()).equals(entity)){
	    		System.out.println("  • Pair entity-nonce already exists: " + entity +" - "+ inboundNonce);
	        	return true;
	        }
	        it.remove();
	    }
	    
	    nonceReceivedHash.put(inboundNonce, entity);
		System.out.println("  • Nonce successfully checked! This message is fresh! ");
	    return false;
	}
	
	
	public boolean checkoutboundNonce(long outboundNonce){
		if(outboundNonce == 0)
			return true;
		for(long nonce : nonceSentList)
			if(nonce == outboundNonce)
				return true;
		return false;
	}
	
	public long generateNonce(){
		
		long outboundNonce = new BigInteger(64, new Random()).longValue();

		while(checkoutboundNonce(outboundNonce))
			outboundNonce = new BigInteger(64, new Random()).longValue();
		setCount(getCount()+1);
		return outboundNonce;
	}


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}
	
}
