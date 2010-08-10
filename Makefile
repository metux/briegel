
PKG_CONFIG?=pkg-config
ANT?=ant

PREFIX     ?= $(HOME)/.usr/jbriegel2
EPREFIX    ?= $(PREFIX)
LIBDIR     ?= $(EPREFIX)/lib
LIBEXECDIR ?= $(EPREFIX)/libexec
BINDIR     ?= $(EPREFIX)/bin
SYSCONFDIR ?= $(PREFIX)/etc
DATADIR    ?= $(PREFIX)/share
JVM        ?= java

JMETUX_CLASSPATH  = $(shell $(PKG_CONFIG) --variable=classpath jar.metux-java)
UNITOOL_CLASSPATH = $(shell $(PKG_CONFIG) --variable=classpath jar.unitool)

JAR_RT_JBRIEGEL=$(DATADIR)/briegel/jbriegel.jar

UNITOOL_SCRIPT_PKG_CONFIG_FIXUP = $(shell $(PKG_CONFIG) jar.unitool --variable script_pkg_config_fixup)
UNITOOL_SCRIPT_RUN_UNITOOL      = $(shell $(PKG_CONFIG) jar.unitool --variable script_run_unitool)
UNITOOL_SCRIPT_RUN_LIBTOOL      = $(shell $(PKG_CONFIG) jar.unitool --variable script_run_libtool)

all:		build

build:		config.mk
	make -C jbriegel	build
	make -C porcelain	build

install:	install-conf
	make -C jbriegel	install
	make -C porcelain	install

.PHONY:	config.mk

config.mk:
	@(  echo "ANT=                            ?= $(ANT)" ; \
	    echo "PKG_CONFIG                      ?= $(PKG_CONFIG)" ; \
	    echo "BRIEGEL_PREFIX                  ?= $(PREFIX)" ; \
	    echo "BRIEGEL_LIBDIR                  ?= $(LIBDIR)" ; \
	    echo "BRIEGEL_BINDIR                  ?= $(BINDIR)" ; \
	    echo "BRIEGEL_LIBEXECDIR              ?= $(LIBEXECDIR)" ; \
	    echo "BRIEGEL_CONFDIR                 ?= $(SYSCONFDIR)" ; \
	    echo "BRIEGEL_JVM                     ?= $(JVM)" ; \
	    echo "BRIEGEL_DATADIR                 ?= $(DATADIR)" ; \
	    echo "JMETUX_CLASSPATH                ?= $(JMETUX_CLASSPATH)" ; \
	    echo "UNITOOL_CLASSPATH               ?= $(UNITOOL_CLASSPATH)" ; \
	    echo "JAR_RT_JBRIEGEL                 ?= $(JAR_RT_JBRIEGEL)" ; \
	    echo "UNITOOL_SCRIPT_PKG_CONFIG_FIXUP ?= $(UNITOOL_SCRIPT_PKG_CONFIG_FIXUP)" ; \
	    echo "UNITOOL_SCRIPT_RUN_UNITOOL      ?= $(UNITOOL_SCRIPT_RUN_UNITOOL)" ; \
	    echo "UNITOOL_SCRIPT_RUN_LIBTOOL      ?= $(UNITOOL_SCRIPT_RUN_LIBTOOL)" ; \
	) > $@

install-conf:
	mkdir -p $(DESTDIR)$(SYSCONFDIR)
	ln -sf `pwd`/conf $(DESTDIR)$(SYSCONFDIR)/briegel

clean:	config.mk
	make -C jbriegel	clean
	make -C porcelain	clean
	rm -f config.mk
