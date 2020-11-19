#--------------------------------------------------------------
#
# This is the makefile for the min-max function.
#
#--------------------------------------------------------------

CFLAGS = -O3 --CLASSPATH=src:modules/jnanoxml/src
srcdir=src/com/jmentor/jbackprop
nandir=modules/jnanoxml/src/com/jmentor/jnanoxml
gnudir=resources/make
objdir=temp
outdir=out

SRCOBJ = $(objdir)/mtrain.o $(objdir)/bpnetwork.o $(objdir)/cputime.o $(objdir)/networkrec.o $(objdir)/network.o
NANOBJ = $(objdir)/XMLElement.o $(objdir)/XMLParseException.o
GNUOBJ = $(objdir)/gnufix.o
$(outdir)/jbackprop.exe:  $(SRCOBJ) $(NANOBJ) $(GNUOBJ)
	gcj $(CFLAGS) --main=com.jmentor.jbackprop.MTrain -o $(outdir)/jbackprop.exe $(SRCOBJ) $(NANOBJ) $(GNUOBJ) -lm

$(objdir)/mtrain.o:  $(srcdir)/mtrain.java
	gcj -c $(CFLAGS) -o $@ $(srcdir)/mtrain.java

$(objdir)/bpnetwork.o:  $(srcdir)/bpnetwork.java
	gcj -c $(CFLAGS) -o $@ $(srcdir)/bpnetwork.java

$(objdir)/cputime.o:  $(srcdir)/cputime.java
	gcj -c $(CFLAGS) -o $@ $(srcdir)/cputime.java

$(objdir)/networkrec.o:  $(srcdir)/networkrec.java
	gcj -c $(CFLAGS) -o $@ $(srcdir)/networkrec.java

$(objdir)/network.o:  $(srcdir)/network.java
	gcj -c $(CFLAGS) -o $@ $(srcdir)/network.java

$(objdir)/XMLElement.o:  $(nandir)/XMLElement.java
	gcj -c $(CFLAGS) -o $@ $(nandir)/XMLElement.java

$(objdir)/XMLParseException.o:  $(nandir)/XMLParseException.java
	gcj -c $(CFLAGS) -o $@ $(nandir)/XMLParseException.java

$(objdir)/gnufix.o:  $(gnudir)/gnufix.java
	gcj -c $(CFLAGS) -o $@ $(gnudir)/gnufix.java

clean:
	rm -f $(outdir)/jbackprop.exe $(objdir)/*.o
