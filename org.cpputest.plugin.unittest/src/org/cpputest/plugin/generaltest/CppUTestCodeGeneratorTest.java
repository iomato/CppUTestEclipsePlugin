package org.cpputest.plugin.generaltest;

import static org.junit.Assert.*;

import org.cpputest.codeGenerator.CppCode;
import org.cpputest.codeGenerator.CppUTestCodeGenerator;
import org.cpputest.codeGenerator.Stubber;
import org.cpputest.parser.SourceCodeReader;
import org.cpputest.parser.impl.Token;
import org.cpputest.parser.langunit.CppLangFunctionSignature;
import org.cpputest.parser.langunit.SignatureBuilder;
import org.eclipse.jface.text.Position;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class CppUTestCodeGeneratorTest {
	final String SOURCE_CODE = "code";
	final CppCode code1 = new CppCode("expected ");
	final CppCode code2 = new CppCode("stub code");
	final CppCode expected_code = new CppCode("expected stub code");
	Mockery context = new JUnit4Mockery();
	
	@Test
	public void testGenerateEmpty() {
		final SourceCodeReader reader = context.mock(SourceCodeReader.class);
		final Stubber stubber = context.mock(Stubber.class);
		final Iterable<?> units = context.mock(Iterable.class);
		final CppLangFunctionSignature s1 = new CppLangFunctionSignature(null);
		final CppLangFunctionSignature s2 = new CppLangFunctionSignature(null);
		
		context.checking(new Expectations() {{
			oneOf(reader).signatures(SOURCE_CODE); will(returnValue(units));
			oneOf(units).iterator(); will(returnIterator(s1,s2));
			oneOf(stubber).getEmptyCStub(s1); will(returnValue(code1));
			oneOf(stubber).getEmptyCStub(s2); will(returnValue(code2));
		}});
		
		CppUTestCodeGenerator cpputest = new CppUTestCodeGenerator(reader, stubber);
		assertEquals(expected_code, cpputest.getEmptyStubOfCode(SOURCE_CODE));
	}
	@Test
	public void testGenerateEmptyAtPosition() {
		final int OFFSET = 10;
		final SourceCodeReader reader = context.mock(SourceCodeReader.class);
		final Stubber stubber = context.mock(Stubber.class);
		final Iterable<?> units = context.mock(Iterable.class);
		final CppLangFunctionSignature s1 = new SignatureBuilder("void")
				.withBeginOffset(OFFSET-4)
				.addToFunctionDeclaration(Token.token("foo"))
				.withEndOffset(OFFSET - 2)
				.build();
		final CppLangFunctionSignature s2 = new SignatureBuilder("void")
				.withBeginOffset(OFFSET-1)
				.addToFunctionDeclaration(Token.token("bar"))
				.withEndOffset(OFFSET + 1)
				.build();
		context.checking(new Expectations() {{
			oneOf(reader).signatures(SOURCE_CODE); will(returnValue(units));
			oneOf(units).iterator(); will(returnIterator(s1,s2));
			oneOf(stubber).getEmptyCStub(s2); will(returnValue(code2));
		}});
		
		CppUTestCodeGenerator cpputest = new CppUTestCodeGenerator(reader, stubber);
		assertEquals(code2, cpputest.getEmptyStubOfCodeAtPosition(SOURCE_CODE, OFFSET));
	}
}
