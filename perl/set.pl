#!perl

use strict;
use warnings;
use Cache::Memcached::Fast::Safe;
use Getopt::Long qw( :config posix_default no_ignore_case bundling );

my $config = do 'config.pl';

GetOptions(
    'ketama|ketama-points|k=i' => \$config->{ketama_points},
    'namespace|ns|p=s'         => \$config->{namespace},
) or die 'Invalid option';

my $memd = Cache::Memcached::Fast::Safe->new($config);

for my $i (1..20) {
    my $key = sprintf("key%d", $i);
    my $oval = $memd->get($key);
    $oval = '(undef)' unless defined $oval;
    $memd->set($key, 'perl');
    my $nval = $memd->get($key);
    $nval = '(undef)' unless defined $nval;
    printf "%s: %s => %s\n", $key, $oval, $nval;
}
