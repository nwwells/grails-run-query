import groovy.sql.Sql

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript('_GrailsBootstrap')

target(main: 'Run Hibernate HQL statement') {
	depends(bootstrap) //currently depending on bootstrap to get injected datasource.

	def dataSource = appCtx.getBean('dataSource')
	Sql sql = new Sql(dataSource)
	def args
	def query
	if (argsMap.file) {
		query = queryFromFile(argsMap)
		args = argsMap.params
	} else {
		query = argsMap.params.remove(0)
	}
	
	def paramCount = query.count("?") 
	def error = "The script expected $paramCount arguments and you provided ${args.size()}."
	if (paramCount > args.size()) {
		event "StatusError", ["- Not enough arguments: $error"]
		System.exit(3)
	} else if (paramCount < args.size()) {
		event "StatusError", ["- Too many arguments: $error The argument list will be trimmed"]
		args = args[0..<paramCount]
	}
	event("StatusUpdate", ["Running query '${query.replace('\n', '; ')}' with args $args"])
	def rows = sql.rows(query, args)
	rows.each { println it }

}

queryFromFile = { argsMap ->
	def query = ""
	File sqlScript = new File(argsMap.file)
	if (!sqlScript.exists()) {
		event("StatusError", ["- The specified query script could not be found (${sqlScript.absolutePath})"])
		System.exit 1
	} else if (!sqlScript.canRead()) {
		event("StatusError", [
			"- The current user doesn't have read access to the specified query script (${sqlScript.absolutePath})"])
		System.exit 2
	} else {
		query = sqlScript.text
	}
	return query
}

setDefaultTarget(main)