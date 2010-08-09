
PKG_CONFIG?=pkg-config
ANT?=ant
GCJ?=gcj

PREFIX     ?= $(HOME)/.usr/jbriegel2
EPREFIX    ?= $(PREFIX)
LIBDIR     ?= $(EPREFIX)/lib
LIBEXECDIR ?= $(EPREFIX)/libexec
BINDIR     ?= $(EPREFIX)/bin
SYSCONFDIR ?= $(PREFIX)/etc
DATADIR    ?= $(PREFIX)/share
JVM        ?= java

BRIEGEL_GCJ ?= true

JMETUX_CLASSPATH  = $(shell $(PKG_CONFIG) --variable=classpath jar.metux-java)
JMETUX_JAR        = $(shell $(PKG_CONFIG) --variable=jarfile   jar.metux-java)
UNITOOL_CLASSPATH = $(shell $(PKG_CONFIG) --variable=classpath jar.unitool)
UNITOOL_JAR       = $(shell $(PKG_CONFIG) --variable=jarfile   jar.unitool)

JAR_RT_JBRIEGEL=$(DATADIR)/briegel/jbriegel.jar

UNITOOL_SCRIPT_PKG_CONFIG_FIXUP = $(shell $(PKG_CONFIG) jar.unitool --variable script_pkg_config_fixup)
UNITOOL_SCRIPT_RUN_UNITOOL      = $(shell $(PKG_CONFIG) jar.unitool --variable script_run_unitool)
UNITOOL_SCRIPT_RUN_LIBTOOL      = $(shell $(PKG_CONFIG) jar.unitool --variable script_run_libtool)

all:		build

build:		config.mk
	make -C jbriegel	build
	make -C porcelain	build
	if $(BRIEGEL_GCJ) ; then make -C gcj	build ; fi

install:	install-conf
	make -C jbriegel	install
	make -C porcelain	install
	if $(BRIEGEL_GCJ) ; then make -C gcj	install ; fi

.PHONY:	config.mk

config.mk:
	@(  echo "ANT=                            ?= $(ANT)" ; \
	    echo "PKG_CONFIG                      ?= $(PKG_CONFIG)" ; \
	    echo "GCJ                             ?= $(GCJ)" ; \
	    echo "BRIEGEL_PREFIX                  ?= $(PREFIX)" ; \
	    echo "BRIEGEL_LIBDIR                  ?= $(LIBDIR)" ; \
	    echo "BRIEGEL_BINDIR                  ?= $(BINDIR)" ; \
	    echo "BRIEGEL_LIBEXECDIR              ?= $(LIBEXECDIR)" ; \
	    echo "BRIEGEL_CONFDIR                 ?= $(SYSCONFDIR)" ; \
	    echo "BRIEGEL_JVM                     ?= $(JVM)" ; \
	    echo "BRIEGEL_DATADIR                 ?= $(DATADIR)" ; \
	    echo "BRIEGEL_GCJ                     ?= $(BRIEGEL_GCJ)" ; \
	    echo "JMETUX_CLASSPATH                ?= $(JMETUX_CLASSPATH)" ; \
	    echo "JMETUX_JAR                      ?= $(JMETUX_JAR)" ; \
	    echo "UNITOOL_CLASSPATH               ?= $(UNITOOL_CLASSPATH)" ; \
	    echo "UNITOOL_JAR                     ?= $(UNITOOL_JAR)" ; \
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
	make -C gcj		clean
	rm -f config.mk
