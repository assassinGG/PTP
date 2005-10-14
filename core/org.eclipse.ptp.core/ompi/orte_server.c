#include <getopt.h>
#include <proxy.h>
#include <proxy_tcp.h>
#include <handler.h>

#define DEFAULT_PROXY		"tcp"

int ORTEIsShutdown(void);

int ORTERun(char **);
int OMPIGetJobs(char **);
int OMPIGetProcesses(char **);
int OMPIGetProcessAttribute(char **);
int OMPIGetMachines(char **);
int OMPIGetNodes(char **);
int OMPIGetNodeAttribute(char **);
int OMPIGetNodemachineID(char **);
int OMPIQuit(void);

int shutdown = 0;

static proxy_svr_helper_funcs helper_funcs = {
	NULL,					// newconn() - can be used to reject connections
	NULL,					// numservers() - if there are multiple servers, return the number
	ORTEIsShutdown,			// shutdown_completed() - proxy will not complete until this returns true
	RegisterFileHandler,		// regfile() - call to register a file handler
	UnregisterFileHandler,	// unregfile() - called to unregister file handler
	RegisterEventHandler,		// regeventhandler() - called to register the proxy event handler
	OMPIQuit					// quit() - called when quit message received
};

static proxy_svr_commands command_tab[] = { 
	{"RUN", 		ORTERun},
	{"GETJOBS", 	OMPIGetJobs},
	{"GETPROCS",	OMPIGetProcesses},
	{"GETATTR",	OMPIGetProcessAttribute},
	{"GETMACHS",	OMPIGetMachines},
	{"GETNODES",	OMPIGetNodes},
	{"GETNATTR",	OMPIGetNodeAttribute},
	{"GETNMID",	OMPIGetNodemachineID},
	{NULL,		NULL}
};

static struct option longopts[] = {
	{"proxy",			required_argument,	NULL, 	'P'}, 
	{"port",				required_argument,	NULL, 	'p'}, 
	{"host",				required_argument,	NULL, 	'h'}, 
	{NULL,				0,					NULL,	0}
};

int
ORTECheckErrorCode(int type, int rc)
{
	if(rc != ORTE_SUCCESS) {
		GenerateEvent(type, ORTE_ERROR_NAME(rc));
		return 1;
	}
	
	return 0;
}

int
ORTEInit(void)
{
	int rc;
	
	/* this makes the orte_init() fail if the orte daemon isn't
	 * running */
	putenv("OMPI_MCA_orte_univ_exist=1");
	
	rc = orte_init(true);
	
	if(ORTECheckErrorCode(ORTE_INIT, rc)) return 1;
	
	return 0;
}

/* this finalizes the registry */
int
ORTEFinalize(void)
{
	int rc;
	
	opal_mutex_lock(&opal_event_lock);
	rc = orte_finalize();
	opal_mutex_unlock(&opal_event_lock);
	
	if(ORTECheckErrorCode(ORTE_FINALIZE, rc)) return 1;
	
	return 0;
}

/* this tells the daemon to exit */
int
ORTEShutdown(void)
{
	ompi_sendcmd(ORTE_DAEMON_EXIT_CMD);
	shutdown++;
	return 0;
}

int
ORTEIsShutdown(void)
{
	return shutdown != 0;
}

/* this is the event progress bit.  we'll have to figure out how to hook
 * this in correctly */
int
ORTEProgress(void)
{
	fd_set			rfds;
	fd_set			wfds;
	fd_set			efds;
	int				res;
	int				nfds = 0;
	struct timeval	tv;
	handler *		h;

	/***********************************
	 * First: Check for any file events
	 */
	 
	/*
	 * Set up fd sets
	 */
	FD_ZERO(&rfds);
	FD_ZERO(&wfds);
	FD_ZERO(&efds);
	
	for (SetHandler(); (h = GetHandler()) != NULL; ) {
		if (h->htype == HANDLER_FILE) {
			if (h->file_type & READ_FILE_HANDLER)
				FD_SET(h->fd, &rfds);
			if (h->file_type & WRITE_FILE_HANDLER)
				FD_SET(h->fd, &wfds);
			if (h->file_type & EXCEPT_FILE_HANDLER)
				FD_SET(h->fd, &efds);
			if (h->fd > nfds)
				nfds = h->fd;
		}
	}
	
	tv = TIMEOUT;
	
	for ( ;; ) {
		res = select(nfds+1, &rfds, &wfds, &efds, &tv);
	
		switch (res) {
		case INVALID_SOCKET:
			if ( errno == EINTR )
				continue;
		
			perror("socket");
			return PROXY_RES_ERR;
		
		case 0:
			/*
			 * Timeout.
			 */
			 break;
			 		
		default:
			for (SetHandler(); (h = GetHandler()) != NULL; ) {
				if (h->htype == HANDLER_FILE
					&& ((h->file_type & READ_FILE_HANDLER && FD_ISSET(h->fd, &rfds))
						|| (h->file_type & WRITE_FILE_HANDLER && FD_ISSET(h->fd, &wfds))
						|| (h->file_type & EXCEPT_FILE_HANDLER && FD_ISSET(h->fd, &efds)))
					&& h->file_handler(h->fd, h->data) < 0)
					return PROXY_RES_ERR;
			}
			
		}
	
		break;
	}
	
	opal_mutex_lock(&opal_event_lock);
	opal_event_loop(0);
	opal_mutex_unlock(&opal_event_lock);
	
	return PROXY_RES_OK;
}

/* terminate a job, given a jobid
 * EVENT RETURN:
 *   type = TERMINATE_JOB
 *   data = "success" ?
int
ORTETerminateJob(int jobid)
{
	int rc;
	
	rc = orte_rmgr.terminate_job(jobid);
	if(ORTECheckErrorCode(ORTE_TERMINATE_JOB, rc)) return 1;
	
	return 0;
}

/* spawn a job with the given executable path and # of procs.
 * EVENT RETURN:
 *   type = RUN
 *   data = "jobid" (example: "848")
 */
int
ORTERun(char **args)
{
	int rc; 
	
	int i;
	orte_jobid_t jobid = ORTE_JOBID_MAX;
	char pgm_name[128], cwd[128];
	char *c;
	orte_app_context_t **apps;
	int num_apps;
	char *exec_path = args[1];
	int num_procs = atoi(args[2]);

	c = rindex(app, '/');

	strncpy(pgm_name, c + 1, strlen(c));
	strncpy(cwd, app, c - app + 1);
	cwd[c-app+1] = '\0';

	/* hard coded test for spawning just 1 job (JOB not PROCESSES!) */
	num_apps = 1;

	/* format the app_context_t struct */
	apps = malloc(sizeof(orte_app_context_t *) * num_apps);
	apps[0] = OBJ_NEW(orte_app_context_t);
	apps[0]->num_procs = num_procs;
	apps[0]->app = strdup(app);
	apps[0]->cwd = strdup(cwd);
	/* no special environment variables */
	apps[0]->num_env = 0;
	apps[0]->env = NULL;
	/* no special mapping of processes to nodes */
	apps[0]->num_map = 0;
	apps[0]->map_data = NULL;
	/* setup argv */
	apps[0]->argv = (char **)malloc(2 * sizeof(char *));
	apps[0]->argv[0] = strdup(pgm_name);
	apps[0]->argv[1] = NULL;

	apps[0]->argc = 1;

	/*
	printf("Spawning %d processes of job '%s'\n", apps[0]->num_procs, apps[0]->app);
	fflush(stdout);
	*/
	
	opal_mutex_lock(&opal_event_lock);
	/* calls the ORTE spawn function with the app to spawn.  Return the
	 * jobid assigned by the registry/ORTE.  Passes a callback function
	 * that ORTE will call with state change on this job */
	rc = orte_rmgr.spawn(apps, num_apps, &jobid, job_state_callback);
	opal_mutex_unlock(&opal_event_lock);
	
	
	
	if(ORTECheckErrorCode(ORTE_RUN, rc)) return 1;
	
	/* generate an event stating what the new/assigned job ID is.
	 * The caller must record this and use this as an identifier to get
	 * information about a job */
	GenerateEvent(ORTE_RUN, jobid);
	
	return PROXY_RES_OK;
}

static void
job_state_callback(orte_jobid_t jobid, orte_proc_state_t state)
{
	handler *		h;
	proxy_event *	e;
	
	/* not sure yet how we want to handle this callback, what events
	 * we want to generate, but here are the states that I know of
	 * that a job can go through.  I've watched ORTE call this callback
	 * with each of these states.  We'll want to come in here and
	 * generate events where appropriate */
	
	printf("job_state_callback(%d)\n", jobid);
	switch(state) {
		case ORTE_PROC_STATE_INIT:
			printf("    state = ORTE_PROC_STATE_INIT\n");
			break;
		case ORTE_PROC_STATE_LAUNCHED:
			printf("    state = ORTE_PROC_STATE_LAUNCHED\n");
			break;
		case ORTE_PROC_STATE_AT_STG1:
			printf("    state = ORTE_PROC_STATE_AT_STG1\n");
			break;
		case ORTE_PROC_STATE_AT_STG2:
			printf("    state = ORTE_PROC_STATE_AT_STG2\n");
			break;
		case ORTE_PROC_STATE_RUNNING:
			printf("    state = ORTE_PROC_STATE_RUNNING\n");
			break;
		case ORTE_PROC_STATE_AT_STG3:
			printf("    state = ORTE_PROC_STATE_AT_STG3\n");
			break;
		case ORTE_PROC_STATE_FINALIZED:
			printf("    state = ORTE_PROC_STATE_FINALIZED\n");
			break;
		case ORTE_PROC_STATE_TERMINATED:
			printf("    state = ORTE_PROC_STATE_TERMINATED\n");
			break;
		case ORTE_PROC_STATE_ABORTED:
			printf("    state = ORTE_PROC_STATE_ABORTED\n");
			break;
	}

	// e = NewEvent(...);
	
	for (SetHandler(); (h = GetHandler()) != NULL; ) {
		if (h->htype == HANDLER_EVENT) {
			h->event_handler(e, h->data);
		}
	}
}

/* this is an internal function we'll call from within this, consider
 * it 'private' */
static int 
ptp_ompi_sendcmd(orte_daemon_cmd_flag_t usercmd)
{
	orte_buffer_t *cmd;
	orte_daemon_cmd_flag_t command;
	int rc;
	orte_process_name_t seed={0,0,0};

	cmd = OBJ_NEW(orte_buffer_t);
	if (NULL == cmd) {
		fprintf(stderr, "console: comm failure\n");
		return 1;
	}
	command = usercmd;

	if (ORTE_SUCCESS != (rc = orte_dps.pack(cmd, &command, 1, ORTE_DAEMON_CMD))) {
		ORTE_ERROR_LOG(rc);
		return 1;
	}
	if (0 > orte_rml.send_buffer(&seed, cmd, ORTE_RML_TAG_DAEMON, 0)) {
		ORTE_ERROR_LOG(ORTE_ERR_COMM_FAILURE);
		return 1;
	}
	OBJ_RELEASE(cmd);
	
	return 0;
}

/* JOB RELATED FUNCTIONS */

/* get a dump of all the jobs known in the universe, this will return
 * an event with a list of jobIDs
 * EVENT RETURN:
 *   type = GET_JOBS
 *   data = [99,195,4555] <-- job IDs */
int
OMPIGetJobs(char **args)
{
	return PROXY_RES_OK;
}

/* given a jobid (valid from OMPIGetJobs()) we request a list of the process
 * IDs associated with that job.  this will return an event with a list of
 * processIDs 
 * EVENT RETURN:
 *   type = GET_PROCESSES
 *   data = [85,86,87,88] <-- those are process IDs
 */
int 
OMPIGetProcesses(char **args)
{
	int jobid = atoi(args[1]);
	return PROXY_RES_OK;
}

/* given a processID (associated with some jobID, but that part is implied)
 * we request the value of an attribute.  the attribute keys are defined
 * elsewhere but an example might be ATTRIB_PROCESS_STATE, ATTRIB_PROCESS_NODE
 * which would be the state of the process (starting, running, terminated,
 * exited, etc) and the nodeID of the node the process is running on, 
 * respectively 
 * EVENT RETURN:
 *   type = GET_NODE_ATTRIBUTE
 *   data = "key=value" (example "state=running", "node=54" <-- 54 = node ID, not necessary node number)
 */
int
OMPIGetProcessAttribute(char **args)
{
	int procid = atoi(args[1]);
	char *attrib = args[2];
	
	return PROXY_RES_OK;
}

/* MONITORING RELATED FUNCTIONS */

/* generates an event which returns a list of all the machineIDs know to
 * the universe.
 * EVENT RETURN:
 *   type = GET_MACHINES
 *   data = [2,3,4,5] <--- those are machine IDs
 */
int
OMPIGetMachines(char **args)
{
	return PROXY_RES_OK;
}

/* given a machine ID, this generates an event which contains a list of
 * all the nodeIDs associated with the given machineID
 * EVENT RETURN:
 *   type = GET_NODES
 *   data = [10,11,12,13,14,15] <-- those are node IDs
 */
int
OMPIGetNodes(char **args)
{
	int machineid = atoi(args[1]);
	return PROXY_RES_OK;
}

/* given a nodeid and an attribute key this generates an event with
 * the attribute's value.  sample attributes might be ATTRIB_NODE_STATE
 * or ATTRIB_NODE_OS which might return up, down, booting, or error and
 * linux, solaris, windows, osx, etc respectively
 * EVENT RETURN:
 *   type = GET_NODE_ATTRIBUTE
 *   data = "key=value" (example "state=down", "user=ndebard")
 */
int
OMPIGetNodeAttribute(char **args)
{
	int nodeid = atoi(args[1]);
	char *attrib = args[2];
	
	return PROXY_RES_OK;
}

/* perhaps we don't need this function.
 * this takes a given nodeid and determines the machineID that it is
 * contained within.  in theory this can be inferred by calling getMachines
 * and getNodes(some_machine_id).  once the model is populated, we should
 * have a relationship between the two but this MAY be helpful ... really
 * it depends on whether we want helper functions at this level or up at
 * the Java level - probably in my opinion we want them at the Java level 
 * EVENT RETURN:
 *   type = GET_NODE_MACHINE_ID
 *   data = "nodeid=machineid" (example node 1000 owned by machine 4 -> "1000=4")
 */ 
int
OMPIGetNodeMachineID(char **args)
{
	int nodeid = atoi(args[1]);
	
	return PROXY_RES_OK;
}

int
OMPIQuit(void)
{
	return PROXY_RES_OK;
}

void
server(proxy *p)
{
	void *	proxy_data;
	
	proxy_svr_init(p, helper_funcs, command_tab, &proxy_data);
	
	proxy_svr_connect(p, host, port, proxy_data);

	
	for (;;) {
		if (ORTEProgress() != PROXY_RES_OK ||
			proxy_svr_progress(p, proxy_data) != PROXY_RES_OK)
			break;
	}
	
	proxy_svr_finish(p, proxy_data);
}

int
main(int argc, char *argv[])
{
	int				ch;
	int				port = PROXY_TCP_PORT;
	char *			host = "localhost";
	char *			proxy_str = DEFAULT_PROXY;
	proxy *			p;
	
	while ((ch = getopt_long(argc, argv, "P:p:h:", longopts, NULL)) != -1)
	switch (ch) {
	case 'P':
		proxy_str = optarg;
		break;
	case 'p':
		port = atoi(optarg);
		break;
	case 'h':
		host = optarg;
		break;
	default:
		fprintf(stderr, "orte_server [--proxy=proxy] [--host=host_name] [--port=port]\n");
		return 1;
	}
	
	if (find_proxy(proxy_str, &p) < 0) {
		fprintf(stderr, "No such proxy: \"%s\"\n", proxy_str);
		return 1;
	}

	if (!ORTEInit()) {
		fprintf(stderr, "Faild to initialize ORTE\n");
		return 1;
	}
	
	server();
	
	ORTEFinalize();
	
	return 0;
}