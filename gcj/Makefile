
include ../config.mk

JAR_JBRIEGEL=../jbriegel/.build/jbriegel.jar

COMMAND_NAMES=\
	cf_query		\
	build			\
	build_all_variants	\
	buildall		\
	check_update		\
	create_image		\
	fetch_source		\
	install_port		\
	setup_sysroot

COMMAND_TARGETS=$(addprefix cmd.,$(COMMAND_NAMES))

LIBJBRIEGEL_LIBNAME=jbriegel
LIBJBRIEGEL_SONAME=lib$(LIBJBRIEGEL_LIBNAME).so
LIBJBRIEGEL=lib$(LIBJBRIEGEL_LIBNAME).so
LIBJBRIEGEL_VER=$(LIBJBRIEGEL_SONAME).so.1

build:		$(COMMAND_TARGETS)

install:	$(LIBJBRIEGEL) $(COMMAND_TARGETS)
	mkdir -p $(DESTDIR)$(BRIEGEL_LIBEXECDIR)/briegel $(DESTDIR)$(BRIEGEL_LIBDIR)
	cp $(COMMAND_TARGETS) $(DESTDIR)$(BRIEGEL_LIBEXECDIR)/briegel
	cp -p $(LIBJBRIEGEL) $(DESTDIR)$(BRIEGEL_LIBDIR)

clean:
	rm -f $(COMMAND_TARGETS) $(LIBJBRIEGEL) $(LIBJBRIEGEL_VER)

$(LIBJBRIEGEL): $(JMETUX_JAR) $(UNITOOL_JAR) $(JAR_JBRIEGEL)
	$(GCJ) -L. -fPIC -shared -Xlinker -soname -Xlinker $(LIBJBRIEGEL_SONAME) -o $@ $?

$(COMMAND_TARGETS):		$(LIBJBRIEGEL)
	$(GCJ) -L. -fPIC -classpath $(JAR_JBRIEGEL):$(JMETUX_CLASSPATH) --main=org.de.metux.briegel.$@ -o $@ -l$(LIBJBRIEGEL_LIBNAME)
