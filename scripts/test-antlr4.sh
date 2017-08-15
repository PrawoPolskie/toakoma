for file in $(find src/test/resources/isap/ -name after_leading_spaces.xml -print);
do parent=$(basename $(dirname $file));
  cp -f src/test/resources/isap/$parent/after_leading_spaces.xml src/test/resources/grammar/$parent.xml;
done
