package org.apache.sling.sitebuilder.api;

public enum ModifyServletOperation {
	ORDERBEFORE, ORDERAFTER, ORDERWITHINFIRST, ORDERWITHINLAST, ADDBEFORE, ADDAFTER, ADDWITHINFIRST, ADDWITHINLAST, DELETE;

	public static ModifyServletOperation lookup(String anOperation) {
		for (ModifyServletOperation op : ModifyServletOperation.values()) {
			if (op.name().equalsIgnoreCase(anOperation)) {
				return op;
			}
		}
		return null;
	}
	
	public boolean isAddOperation(){
		return this == ADDBEFORE || this == ADDAFTER || this == ADDWITHINFIRST || this == ADDWITHINLAST;
	}
	
	public boolean isOrderOperation(){
		return this == ORDERBEFORE || this == ORDERAFTER || this == ORDERWITHINFIRST || this == ORDERWITHINLAST;
	}
}