package scratch;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import scratch.grammar1.Grammar1Lexer;
import scratch.grammar1.Grammar1Parser;

import java.util.Objects;

public class AntrlExample {
    @Test
    void grammar1test() throws Exception {
        System.out.println("grammar1test");

        CharStream charStream = CharStreams.fromStream(
                Objects.requireNonNull(getClass().getResourceAsStream("/grammar1.dsl")));
        Grammar1Lexer lexer = new Grammar1Lexer(charStream);
        Grammar1Parser parser = new Grammar1Parser(new CommonTokenStream(lexer));

        parser.argument();

    }
}