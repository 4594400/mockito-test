package ua.home.classes;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import ua.home.interfaces.Foo;
import ua.home.interfaces.FooVoid;
import ua.home.interfaces.impl.FooImpl;
import ua.home.interfaces.impl.FooVoidImpl;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MockitoTestRunner {
    @Mock
    private Foo foo;
    @InjectMocks
    private Bar bar = new Bar(null);

    @Test
    public void annotatedMocking() {
        bar.bar("qwe");
        verify(foo).foo("qwe");
    }

    @Test
    public void simpleMocking() {
        Foo foo = mock(Foo.class);
        Bar bar = new Bar(foo);

        bar.bar("qwe");
        verify(foo).foo("qwe");
    }

    @Test
    public void ignoreParameter() {
        bar.bar("some String");
        verify(foo).foo(anyString());
    }

    @Test
    public void stubParameter() {
        when(foo.foo("qwe")).thenReturn("asd");
        stub(foo.foo("qwe")).toReturn("asd");
        doReturn("asd").when(foo).foo("qwe");

        assertEquals("asd", bar.bar("qwe"));
    }

    @Test
    public void stubParameterWrong() {
        when(foo.foo("qwe")).thenReturn("asd");
        assertNull(bar.bar("zxc")); // wrong
    }

    @Test
    public void ignoreString() {
        when(foo.foo(anyString())).thenReturn("asd");

        assertEquals("asd", bar.bar("qwe"));
        assertEquals("asd", bar.bar("zxc"));
    }

    @Test
    public void parameterMatches() {
        bar.bar("qwe");
        verify(foo).foo(matches("...")); // "." is any symbol
    }

    @Test
    public void scenarioMatches() {
        when(foo.foo(matches("...."))).thenReturn("asd");
        assertEquals("asd", bar.bar("qwer"));
        assertNull(bar.bar("qwe"));
    }

    @Test
    public void basicMatcher() {
        when(foo.foo(endsWith("we"))).thenReturn("asd");
        when(foo.foo(startsWith("q"))).thenReturn("asd");
        when(foo.foo(contains("qw"))).thenReturn("asd");

        assertEquals("asd", bar.bar("qwe"));
    }

    @Test
    public void basicMatcherVerify() {
        bar.bar("qwe");
        verify(foo).foo(endsWith("we"));
        verify(foo).foo(startsWith("qw"));
        verify(foo).foo(contains("qw"));
    }

    @Test
    public void equals() {
        when(foo.foo(eq("qwe"))).thenReturn("asd");
        when(foo.foo("qwe")).thenReturn("asd"); // the same
        assertEquals("asd", bar.bar("qwe"));
    }

    // ----------------------- OWN MATCHER ---------------------------------
    @Test
    public void testOwnMatcher() {
        when(foo.foo(argThat(isQwe()))).thenReturn("asd");
    }

    private Matcher<String> isQwe() {
        return new ArgumentMatcher<String>() {
            @Override
            public boolean matches(Object argument) {
                return argument.equals("qwe");
            }
        };
    }
    // ---------------------------------- VOID ------------------------------------

    @Test
    public void simpleVoidMocking() {
        FooVoid foo = mock(FooVoid.class);
        BarVoid bar = new BarVoid(foo);

        doNothing().when(foo).foo("qwe");
        bar.bar("qwe");
        verify(foo).foo("qwe");
    }

    @Test(expected = IllegalArgumentException.class)
    public void excluding() {
        FooVoid foo = mock(FooVoid.class);
        BarVoid bar = new BarVoid(foo);

        doThrow(new IllegalArgumentException()).when(foo).foo(anyString());
        doNothing().when(foo).foo("qwe");

        bar.bar("qwe");
        verify(foo).foo("qwe");
        bar.bar("ss");
    }

    @Test(expected = Exception.class)
    public void throwException() {
        Foo foo = mock(Foo.class);
        Bar bar = new Bar(foo);

        when(foo.foo("qwe")).thenThrow(new Exception());
        bar.bar("qwe");
    }

    @Test(expected = Exception.class)
    public void voidThrowException() throws Exception {
        FooVoid foo = mock(FooVoid.class);
        BarVoid bar = new BarVoid(foo);
        doThrow(new Exception()).when(foo).foo("qwe");
        bar.bar("qwe");
    }

    @Test
    public void checkTimes() {
        bar.bar("qwe");
        bar.bar("qwe");
        bar.bar("qwe");
        bar.bar("asd");
        verify(foo, times(3)).foo("qwe");
        verify(foo, atLeastOnce()).foo("qwe");
        verify(foo, never()).foo("zxc");
        verify(foo, atMost(5)).foo(anyString());
    }

    @Test
    public void spyParameter() {
        Foo foo = spy(new FooImpl()); // calls real methods
        Bar bar = new Bar(foo);

        assertEquals("qwe", bar.bar("qwe")); // it was

        when(foo.foo("qwe")).thenReturn("asd"); // after changing
        // or
        doReturn("asd").when(foo).foo("qwe");
        //foo.foo("qwe");
        assertEquals("asd", bar.bar("qwe"));
    }

    @Test
    public void mockWithoutScenario() {
        Foo foo = mock(FooImpl.class);
        Bar bar = new Bar(foo);

        assertNull(bar.bar("qwe"));
        doReturn("asd").when(foo).foo("qwe");
        assertEquals("asd", bar.bar("qwe"));
    }

    @Test
    public void byDefaultReturns() {
        List list = mock(List.class);
        assertEquals(0, list.size());
        assertFalse(list.isEmpty());
        assertNull(list.iterator());
        assertEquals("[]", list.subList(1, 2).toString());
    }

    @Test
    public void stubThenCall() {
        Foo foo = mock(FooImpl.class);
        Bar bar = new Bar(foo);
        when(foo.foo("qwe")).thenReturn("asd").thenCallRealMethod();

        assertEquals("asd", bar.bar("qwe"));
        assertEquals("qwe", bar.bar("qwe"));
        assertEquals("qwe", bar.bar("qwe"));
        assertEquals("qwe", bar.bar("qwe"));
    }

    @Test
    public void voidCallRealMethod() {
        FooVoid foo = mock(FooVoidImpl.class);
        BarVoid bar = new BarVoid(foo);

        doCallRealMethod().when(foo).foo("qwe");
        bar.bar("qwe");
        verify(foo).foo("qwe");
    }

    @Test
    public void someStrongFlow() {
        when(foo.foo("qwe")).thenReturn("asd").thenReturn("sdf");
        //or
        when(foo.foo("qwe")).thenReturn("asd", "sdf");
        assertEquals("asd", bar.bar("qwe"));
        assertEquals("sdf", bar.bar("qwe"));
        assertEquals("sdf", bar.bar("qwe"));
    }

    @Test
    public void someFlow() {
        when(foo.foo("111")).thenReturn("222");
        when(foo.foo("333")).thenReturn("444");

        assertEquals("444", bar.bar("333"));
        assertEquals("222", bar.bar("111"));
        // the order does not matter
    }

    @Test
    public void cumulativeFlow() {
        when(foo.foo("qwe1")).thenReturn("asd").thenReturn("sdf");
        when(foo.foo("qwe2")).thenReturn("asd", "sdf");

        assertEquals("asd", bar.bar("qwe2"));
        assertEquals("asd", bar.bar("qwe1"));
        assertEquals("sdf", bar.bar("qwe1"));
        assertEquals("sdf", bar.bar("qwe2"));
    }

    @Test
    public void twoMockFlow() {
        Foo foo1 = mock(Foo.class);
        Foo foo2 = mock(Foo.class);
        Bar2 bar = new Bar2(foo1, foo2);

        InOrder inOrder = inOrder(foo1, foo2);
        bar.bar("qwe");
        inOrder.verify(foo1).foo("qwe");
        inOrder.verify(foo2).foo("qwe");
    }

    @Test
    public void thenAnswer(){
        when(foo.foo(anyString())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                if (args[0].equals("qwe")) {
                    return "asd";
                } else {
                    return "qwe";
                }   // Object mock = invocation.getMock();
            }       // Object value = invocation.callRealMethod();
        });

        assertEquals("asd", bar.bar("qwe"));
        assertEquals("qwe", bar.bar("asd"));
    }

    @Test(expected = RuntimeException.class)
    public void thenAnswerOnVoid() {
        FooVoid foo = mock(FooVoid.class);
        BarVoid bar = new BarVoid(foo);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new RuntimeException();
            }
        }).when(foo).foo(anyString());

        bar.bar("asd");
    }

    @Test
    public void neverMockCall() {
        //bar.bar("qwe");
        verifyZeroInteractions(foo);
    }

    @Test
    public void neverMockCallAfter() {
        bar.bar("qwe");
        verify(foo).foo("qwe");
        verifyNoMoreInteractions(foo);
        // or verify(foo, only()).foo("qwe");
    }

    @Test
    public void captures(){
        bar.bar("qwe");
        bar.bar("asd");
        bar.bar("zxc");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(foo, times(3)).foo(captor.capture());
        assertEquals("[qwe, asd, zxc]", captor.getAllValues().toString());
    }

    @Test
    public void resetMock(){
        when(foo.foo("qwe")).thenReturn("asd");
        bar.bar("qwe");
        bar.bar("qwe");
        verify(foo, times(2)).foo("qwe");

        reset(foo); // no good practice

        bar.bar("qwe");
        bar.bar("qwe");
        bar.bar("qwe");
        verify(foo, times(3)).foo("qwe");
    }

    @Test
    public void checkUsage(){
        when(foo.foo("qwe")); //.thenReturn("asd"); showHints
        bar.bar("qwe");
        verify(foo, times(1)).foo("qwe");
        assertEquals("asd", bar.bar("qwe"));
        validateMockitoUsage();
    }
}
