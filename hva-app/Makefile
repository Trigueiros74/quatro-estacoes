# Localização de po-uilib.jar: a cópia local do repositório, quando existe, ou
# a instalação do sistema, que é onde está no ambiente de avaliação.
PO_UILIB_DIR := $(shell test -f $(CURDIR)/../po-uilib/po-uilib.jar \
	&& echo $(CURDIR)/../po-uilib || echo /usr/share/java)
JARNAME=hva-app

all:
	(cd src; javac -cp $(PO_UILIB_DIR)/po-uilib.jar:../../hva-core/hva-core.jar -encoding UTF-8 `find . -name \*.java`)
	(cd src; jar cf ../$(JARNAME).jar `find . -name \*.class -o -name \*.java` )

clean:
	$(RM) $(JARNAME).jar `find . -name \*.class`
