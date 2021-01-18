package havis.middleware.utils.threading;

/**
 * Thread manager task
 */
public interface Task extends Runnable {

	/**
	 * @return the group id of this task
	 */
	int getGroupId();

}
