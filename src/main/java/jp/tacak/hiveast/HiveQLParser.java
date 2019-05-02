package jp.tacak.hiveast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.antlr.runtime.RecognitionException;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseError;
import org.apache.hadoop.hive.ql.parse.ParseException;

/**
 * HiveQL Parser
 * Input: HiveQL File
 * Output:
 *  | success -> AST
 *  | fail    -> [line, column]: error information
 */
public class HiveQLParser
{
    public static void main( String[] args )
            throws IOException, NoSuchFieldException, IllegalAccessException, ParseException {

        if(args.length < 1){
            System.out.println("Usage: java -jar HiveAST.jar [hiveQLFile] ");
            return;
        }

        String file = args[0];
        String content = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
        ParseDriver pd = new ParseDriver();

        try {
            ASTNode node = pd.parse(content);
            System.out.println(node.toStringTree());
        } catch (ParseException e) {
            Field errorsField = ParseException.class.getDeclaredField("errors");
            errorsField.setAccessible(true);
            ArrayList<ParseError> errors = (ArrayList<ParseError>) errorsField.get(e);

            Field reField = ParseError.class.getDeclaredField("re");
            reField.setAccessible(true);
            RecognitionException re = (RecognitionException) reField.get(errors.get(0));

            String errorMsg = "[" + re.line + "," + re.charPositionInLine + "]: " + e.getMessage();

            System.out.println(errorMsg);

            throw e;
        }
    }
}
