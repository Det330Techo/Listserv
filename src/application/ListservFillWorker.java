package application;

import java.util.LinkedList;

/**
 *  * <pre>Given a <code>LinkedList</code> of emails and an <code>Action</code> to join or leave, 
 * this class will join or leave the listserv for the given list of emails.</pre>
 * 
 * <pre>This class is thread-safe.</pre>
 * 
 * @author <pre>Travis Carson, C/Capt, AFROTC</pre>
 * <pre>Technology Officer, Detachment 330</pre>
 * <pre>February 2017</pre>
 * 
 */
public class ListservFillWorker extends Thread {

	protected LinkedList<String> list;
	public enum Action {LEAVE, JOIN};
	private Action action;
	
	public ListservFillWorker(LinkedList<String> list, Action action) {
		this.list = list;	
		this.action = action;
	}
	
	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
		while (true) {
			String email;
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
				if (list.isEmpty())
					return; // list is empty
				email = list.remove();
			}
			
			// Join or Leave the email from the listserv.
			switch(action){
				case JOIN: ListservFill.join(email); break;
				case LEAVE: ListservFill.leave(email); break;
			}

		}
	}
}
