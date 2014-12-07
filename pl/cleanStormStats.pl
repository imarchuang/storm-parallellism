#!/usr/bin/perl

use DBI;
use strict;

my $driver = "mysql"; 
my $database = "stormstats";
my $dsn = "DBI:$driver:database=$database;host=localhost";
my $userid = "storm";
my $password = "storm";

my $dbh = DBI->connect($dsn, $userid, $password ) or die $DBI::errstr;

my $sql = $dbh->prepare("SELECT runid, state, startdatetime 
						FROM runstate 
						WHERE runid < (SELECT max(runid) FROM runstate) - ?");
my $range = 100;
$sql->execute($range) or die $DBI::errstr;

my $rows = $sql->rows;
print "Number of rows found : $rows\n";
while (my @row = $sql->fetchrow_array()) {
	my ($runid, $state, $startdatetime) = @row;
	#print "runid = $runid, state = $state, startdatetime = $startdatetime\n";

	#delete the rows table by table
	my @tables = ("cluster", "nimbus", "supervisor","topology","component","executor","topoconfig","stormstats","runstate");
	for(@tables){
		my $table = $_;
		my $statement = "DELETE FROM $table WHERE runid = ?";
		print "$statement $runid\t";
		my $sql_del = $dbh->prepare($statement);
		$sql_del->execute( $runid ) or die $DBI::errstr;
		my $rows_del = $sql_del->rows;
		print "Number of rows deleted : $rows_del\n";
		$sql_del->finish();
		#$dbh->commit or die $DBI::errstr;
	}
}
$sql->finish();
         
#my @tables = ("cluster", "nimbus", "supervisor","topology","component","executor","topoconfig","stormstats");
#for(@tables){
#	print "$_\t";
#}
