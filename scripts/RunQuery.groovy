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

   def args = argsMap.params
   if (argsMap.file) {
      def filename = argsMap.file
   } else {
      def query = args.remove(0)
   }
   def dataSource = appCtx.getBean('dataSource')
   def mysqlQuery = 

   Sql sql = new Sql(dataSource)
   def rows = sql.rows(mysqlQuery)
   rows.each { println it }

   println argsMap

}

setDefaultTarget(main)