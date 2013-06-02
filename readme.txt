Build instruction:
download JOGL.
open the file build.xml in this package. Substitute /PATH/TO/ in /PATH/TO/jogamp-all-platforms/jar/ with the path that you have extracted JOGL
run the command ant.

Here is the layout of this package:
bin/			Source code of this game
src/			the .jar executable file located in.
LICENSE.txt		The license of the game
jogl.LICENSE.txt	License of JOGL, which is a library that this game linked to
gluegen.LICENSE.txt	License of gluegen, which is included in JOGL package
