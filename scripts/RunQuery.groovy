import groovy.sql.Sql

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript('_GrailsBootstrap')

target(main: 'Run Hibernate HQL statement') {
	depends(
			// checkVersion,
			// configureProxy,
			// enableExpandoMetaClass,
			bootstrap //currently depending on bootstrap to get injected datasource.
			)

	def args
	def query
	if (argsMap.file) {
		def filename = argsMap.file
		File sqlScript = new File(argsMap.file)
		if (sqlScript.exists() && sqlScript.canRead()) {
			query = sqlScript.text
			args = argsMap.params
		} else {
			throw new Exception("the sql script specified (${argsMap.file}), either doesn't exist," +
								" can't be read (permissions?), or there's some other bad news.")
		}
	} else {
		query = argsMap.params.remove(0)
	}
	def dataSource = appCtx.getBean('dataSource')
	Sql sql = new Sql(dataSource)
	def rows = sql.rows(query)
	rows.each { println it }

	println argsMap

}

setDefaultTarget(main)