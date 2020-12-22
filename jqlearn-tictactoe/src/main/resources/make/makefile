#--------------------------------------------------------------
#
# This is the makefile for the min-max function.
#
#--------------------------------------------------------------

CFLAGS = -O3 --CLASSPATH=src:modules/jbackprop/src:modules/jnanoxml/src:modules/jrandom/src:modules/jtictactoe/src
srcdir=src/com/jmentor/jqlearn
backdir=modules/jbackprop/src/com/jmentor/jbackprop
nandir=modules/jnanoxml/src/com/jmentor/jnanoxml
randir=modules/jrandom/src/com/jmentor/jrandom
tttdir=modules/jtictactoe/src/com/jmentor/jtictactoe
gnudir=resources/make
objdir=temp
outdir=out

SRCOBJ = $(objdir)/QLearn.o $(objdir)/QMinimax.o $(objdir)/Teach.o
BACKOBJ = $(objdir)/bpnetwork.o $(objdir)/cputime.o $(objdir)/networkrec.o $(objdir)/network.o
NANOBJ = $(objdir)/XMLElement.o $(objdir)/XMLParseException.o
RANOBJ = $(objdir)/RandomElement.o $(objdir)/RandomSeedable.o $(objdir)/Ranlux.o $(objdir)/MersenneTwister.o
TTTOBJ = $(objdir)/Board.o $(objdir)/Game.o $(objdir)/Human.o $(objdir)/Minimax.o $(objdir)/Player.o $(objdir)/Random.o $(objdir)/Train.o
GNUOBJ = $(objdir)/gnufix.o
$(outdir)/jqlearn.exe:  $(SRCOBJ) $(BACKOBJ) $(NANOBJ) $(RANOBJ) $(TTTOBJ) $(GNUOBJ)
	gcj $(CFLAGS) --main=com.jmentor.jqlearn.Teach -o $(outdir)/jqlearn.exe $(SRCOBJ) $(BACKOBJ) $(NANOBJ) $(RANOBJ) $(TTTOBJ) $(GNUOBJ) -lm

$(objdir)/QLearn.o:  $(srcdir)/QLearn.java
	gcj -c $(CFLAGS) -o $@ $(srcdir)/QLearn.java

$(objdir)/QMinimax.o:  $(srcdir)/QMinimax.java
	gcj -c $(CFLAGS) -o $@ $(srcdir)/QMinimax.java

$(objdir)/Teach.o:  $(srcdir)/Teach.java
	gcj -c $(CFLAGS) -o $@ $(srcdir)/Teach.java


$(objdir)/bpnetwork.o:  $(backdir)/bpnetwork.java
	gcj -c $(CFLAGS) -o $@ $(backdir)/bpnetwork.java

$(objdir)/cputime.o:  $(backdir)/cputime.java
	gcj -c $(CFLAGS) -o $@ $(backdir)/cputime.java

$(objdir)/networkrec.o:  $(backdir)/networkrec.java
	gcj -c $(CFLAGS) -o $@ $(backdir)/networkrec.java

$(objdir)/network.o:  $(backdir)/network.java
	gcj -c $(CFLAGS) -o $@ $(backdir)/network.java


$(objdir)/XMLElement.o:  $(nandir)/XMLElement.java
	gcj -c $(CFLAGS) -o $@ $(nandir)/XMLElement.java

$(objdir)/XMLParseException.o:  $(nandir)/XMLParseException.java
	gcj -c $(CFLAGS) -o $@ $(nandir)/XMLParseException.java


$(objdir)/RandomElement.o:  $(randir)/RandomElement.java
	gcj -c $(CFLAGS) -o $@ $(randir)/RandomElement.java

$(objdir)/RandomSeedable.o:  $(randir)/RandomSeedable.java
	gcj -c $(CFLAGS) -o $@ $(randir)/RandomSeedable.java

$(objdir)/Ranlux.o:  $(randir)/Ranlux.java
	gcj -c $(CFLAGS) -o $@ $(randir)/Ranlux.java

$(objdir)/MersenneTwister.o:  $(randir)/MersenneTwister.java
	gcj -c $(CFLAGS) -o $@ $(randir)/MersenneTwister.java


$(objdir)/Board.o:  $(tttdir)/Board.java
	gcj -c $(CFLAGS) -o $@ $(tttdir)/Board.java

$(objdir)/Game.o:  $(tttdir)/Game.java
	gcj -c $(CFLAGS) -o $@ $(tttdir)/Game.java

$(objdir)/Human.o:  $(tttdir)/Human.java
	gcj -c $(CFLAGS) -o $@ $(tttdir)/Human.java

$(objdir)/Minimax.o:  $(tttdir)/Minimax.java
	gcj -c $(CFLAGS) -o $@ $(tttdir)/Minimax.java

$(objdir)/Player.o:  $(tttdir)/Player.java
	gcj -c $(CFLAGS) -o $@ $(tttdir)/Player.java

$(objdir)/Random.o:  $(tttdir)/Random.java
	gcj -c $(CFLAGS) -o $@ $(tttdir)/Random.java

$(objdir)/Train.o:  $(tttdir)/Train.java
	gcj -c $(CFLAGS) -o $@ $(tttdir)/Train.java

$(objdir)/gnufix.o:  $(gnudir)/gnufix.java
	gcj -c $(CFLAGS) -o $@ $(gnudir)/gnufix.java

clean:
	rm -f $(outdir)/jqlearn.exe $(objdir)/*.o
