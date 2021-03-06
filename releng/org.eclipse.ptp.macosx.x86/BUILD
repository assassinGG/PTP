#!/bin/sh
#
# This script is used to build proxy plugins in the workspace
#

# This should get replaced by build script with the actual
# PTP build version number. The script can also be run out of CVS,
# in which case there is no version.
VERSION="_@buildVersion@"
if [ `expr "$VERSION" : "_@[^@]*@"` -gt 0 ]; then
	VERSION=
fi

CMD=configure
if [ $# -gt 0 ]; then
	CMD=$1
fi

KNOWN_PLUGINS="\
	core/org.eclipse.ptp.utils$VERSION\
	core/org.eclipse.ptp.proxy$VERSION\
	debug/org.eclipse.ptp.debug.sdm$VERSION\
	rms/org.eclipse.ptp.rm.ibm.ll.proxy$VERSION\
	rms/org.eclipse.ptp.rm.ibm.pe.proxy$VERSION\
	rms/org.eclipse.ptp.rm.slurm.proxy$VERSION"
	
if [ "$CMD" = "configure" ]; then
	UTILS_CONFIGURE_OPTIONS=""
	PROXY_CONFIGURE_OPTIONS=""
	SDM_CONFIGURE_OPTIONS=""
	IBM_LL_CONFIGURE_OPTIONS=""
	IBM_PE_CONFIGURE_OPTIONS=""
	PBS_CONFIGURE_OPTIONS="--with-pbs=/usr/local/torque"
	SLURM_CONFIGURE_OPTIONS="--with-slurm=/usr/local/slurm"

	set "$UTILS_CONFIGURE_OPTIONS"\
	    "$PROXY_CONFIGURE_OPTIONS"\
	    "$SDM_CONFIGURE_OPTIONS"\
	    "$IBM_LL_CONFIGURE_OPTIONS"\
	    "$IBM_PE_CONFIGURE_OPTIONS"\
	    "$PBS_CONFIGURE_OPTIONS"\
	    "$SLURM_CONFIGURE_OPTIONS"
fi
		
PARENT=$(dirname `dirname $PWD`)
PREFIX=$PWD

if [ ! -d bin ]; then
	mkdir bin
fi

cd $PARENT

for plugin in `echo $KNOWN_PLUGINS`
do
	case "$CMD" in
	configure)
		if [ -e $plugin/configure ]; then
			echo "Building $plugin..."
			(cd $plugin; \
			sh configure --prefix=$PREFIX $1 && \
			chmod +x install-sh && \
			make install)
			echo; echo
		fi
		shift
		;;
	build)
		if [ -e $plugin/Makefile ]; then
			(cd $plugin; make install)
		fi
		;;
	clean)
		if [ -e $plugin/Makefile ]; then
			(cd $plugin; make clean)
		fi
		;;
	esac
done

exit 0

