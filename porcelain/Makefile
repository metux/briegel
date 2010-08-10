
include ../config.mk

PORCELAIN_AUTOGEN_NAMES=\
	lib/briegel/target-commands.sh	\
	lib/briegel/confdb.sh		\
	lib/briegel/massbuild.sh	\
	bin/briegel-create-target	\
	bin/briegel-pkg-config-fixup

all:		build

lib/briegel/%.sh:	lib/briegel/%.sh.in
	@cat $< | sed 's~@BRIEGEL_DEFAULT_ROOT@~$(BRIEGEL_PREFIX)~g;' 	\
	       | sed 's~@BRIEGEL_DEFAULT_JVM@~$(BRIEGEL_JVM)~g;'	\
	       | sed 's~@BRIEGEL_LIBEXECDIR@~$(BRIEGEL_LIBEXECDIR)~g'	\
	       | sed 's~@BRIEGEL_LIBDIR@~$(BRIEGEL_LIBDIR)~g'	\
	       | sed 's~@BRIEGEL_GCJ@~$(BRIEGEL_GCJ)~g'			\
	       | sed 's~@UNITOOL_CLASSPATH@~$(UNITOOL_CLASSPATH)~g;'	\
	       | sed 's~@UNITOOL_SCRIPT_PKG_CONFIG_FIXUP@~$(UNITOOL_SCRIPT_PKG_CONFIG_FIXUP)~g;'	\
	       | sed 's~@JAR_RT_JBRIEGEL@~$(JAR_RT_JBRIEGEL)~g;'	\
	       | sed 's~@JMETUX_CLASSPATH@~$(JMETUX_CLASSPATH)~g;'	\
	    >$@

bin/%:		bin/%.in
	@cat $< | sed 's~@BRIEGEL_DEFAULT_ROOT@~$(BRIEGEL_PREFIX)~g;' 	\
	       | sed 's~@BRIEGEL_DEFAULT_JVM@~$(BRIEGEL_JVM)~g;'	\
	       | sed 's~@BRIEGEL_LIBEXECDIR@~$(BRIEGEL_LIBEXECDIR)~g'	\
	       | sed 's~@BRIEGEL_LIBDIR@~$(BRIEGEL_LIBDIR)~g'	\
	       | sed 's~@BRIEGEL_GCJ@~$(BRIEGEL_GCJ)~g'			\
	       | sed 's~@UNITOOL_CLASSPATH@~$(UNITOOL_CLASSPATH)~g;'	\
	       | sed 's~@UNITOOL_SCRIPT_PKG_CONFIG_FIXUP@~$(UNITOOL_SCRIPT_PKG_CONFIG_FIXUP)~g;'	\
	       | sed 's~@JAR_RT_JBRIEGEL@~$(JAR_RT_JBRIEGEL)~g;'	\
	       | sed 's~@JMETUX_CLASSPATH@~$(JMETUX_CLASSPATH)~g;'	\
	    >$@
	@chmod +x $@

build:	$(PORCELAIN_AUTOGEN_NAMES)

install:	build
	@mkdir -p $(DESTDIR)$(BRIEGEL_LIBDIR) $(DESTDIR)$(BRIEGEL_BINDIR)
	@cp -p -d --parents $(PORCELAIN_AUTOGEN_NAMES) $(DESTDIR)$(BRIEGEL_PREFIX)

clean:	
	@rm -f $(PORCELAIN_AUTOGEN_NAMES)
