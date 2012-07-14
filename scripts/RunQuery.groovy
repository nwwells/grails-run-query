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
   def mysqlQuery = """
SELECT f.id as facility_id, f.name as facility_name, sub.id,
       sub.date, sub.type, sub.nurse_name
FROM ( SELECT a.id, a.date, a.nurse_id, a.type, a.facility_id,
    a.end_date, CONCAT(n.first_name, ' ', n.last_name,
      CASE WHEN a.notes IS NOT NULL
      THEN '*' ELSE '' END) as nurse_name
    FROM assignment as a, user as n
    WHERE date >= '2012-02-15'
      AND date <  '2012-02-16'
      AND nurse_id = n.id
) as sub
RIGHT JOIN facility as f ON sub.facility_id = f.id
ORDER BY facility_name, sub.date, sub.end_date
"""

   Sql sql = new Sql(dataSource)
   def rows = sql.rows(mysqlQuery)
   rows.each { println it }

   println argsMap

}

setDefaultTarget(main)