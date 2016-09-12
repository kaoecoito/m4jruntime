# M4JRuntime

MUMPS Runtime Interpreter implemented in Java

# About

- Created by [Kaoe Coito](https://www.facebook.com/kaoecoito) company's partner [Sistemas Profissionais](http://www.sistemasprofissionais.com.br/).
- Designed to allow our current ERP system for Real estate developed in MUMPS can be ported to Java code.
- The project is in Pre Alpha phase and should not be used in production.
- If you have any questions you can send to my e-mail.

# Objectives

- Create a Virtual Machine to interpret MUMPS code developed in Java.
- Implement the storage of data in PostgreSQL with similar behavior to the Global MUMPS storage.
- Create Java classes that can be seen as MUMPS routines allowing MUMPS code to Java code migration gradually.

# Thanks

- Thanks to [Brian Bray](https://www.linkedin.com/in/bbray) for the initial implementation of MUMPS parser in ANTLR4 and some ideas that inspired me to create the project.
- The M4J project code that was developed by Brian can be found in Github [braylabs/m4j](https://github.com/braylabs/m4j).
- Thanks to [Allison Kaptur](http://aosabook.org/en/500L/a-python-interpreter-written-in-python.html) who explained simply and clearly the main issues on how to implement a Virtual Machine to interpret codes in Bytecode.
