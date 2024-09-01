# Analyzer

Parser & interpreter for a custom programming language.
This project was made for study and research about compiler construction.

## Why "Analyzer"?

1. I'm bad at naming things.
2. This project began as a lexical analysis tool; the parser and interpreter were added later on.
   _I may rename it in the future_

## Types

The language supports these primitive types:

1. `array` (java.util.ArrayList)
2. `bool` (java.lang.Boolean)
3. `float` (java.lang.Double)
4. `int` (java.lang.Long)
5. `string` (java.lang.String)

Numeric literals can be expressed in various forms:

* Bin: `0b00001010`
* Hex: `0xffa500`
* Oct: `0o276`
* Exp: `1.30e5`

## Comments

```
// This is a line comment.

/*
    And this is a
    block comment.
 */
```

## Variables

### Declaring a variable

Use the `:=` operator (borrowed from Go) to declare a new variable in the current scope.

```
var := "Hello";
```

The interpreter will throw a fatal error if you attempt to re-declare a variable within the same scope.

```
var := "Again!"; // Fatal Error: 'var' is already defined.
```

However, you can declare a variable with the same identifier (name) within a nested scope.

```
var := "Outer";

{
    // This is allowed, as the inner-most variable
    // shadows the variable in the parent scope.
    var := "Inner";
    print(var);
}

print(var);
```

### Using a variable

Variables can be re-assigned with the `=` operator.

```
var = "Hello there";
print(var);
```

And the interpreter will throw a fatal error if the variable is undefined.

```
unknown = "???"; // Fatal Error: undefined variable 'unknown'.
```

Variables can store any type of value at any moment:

```
var = [1, 2, 3];
var = true;
var = 3.1415;
var = 2023;
var = "A113";
```

The `is` operator checks if the given value is of a specific type, while the `as` operator
casts the value from one type to another:

```
// Check if the value is a string
if var is string {

   // Cast to integer
   casted := var as int;

}
```

Use the `unset` operator to _remove_ a variable:

```
unset var;
print(var); // Fatal Error: undefined variable 'var'.
```

Arrays follow the usual bracket-style syntax:

```
arr := ["a", "b", "c"];
val := arr[1];
arr[0] = "x";
```

Arrays grow dynamically, so you can append values by using the `[]` syntax (borrowed from PHP):

```
arr := []; // Empty array
arr[] = "hi"; // Append a string (note that you have to use = operator).
print(arr[0]);
```

And with the `unset` operator, you can remove one value from the array (the array will be re-indexed):

```
arr := ["a", "b", "c"];

print(arr[0]); // "a"
unset arr[0];
print(arr[0]); // "b"
```

## Logic operators

| Operator         | Meaning |
|------------------|---------|
| `expr && expr`   | and     |
| `expr \|\| expr` | or      |
| `!expr`          | not     |

## Relational operators

| Operator       | Meaning          |
|----------------|------------------|
| `expr == expr` | equal            |
| `expr != expr` | not equal        |
| `expr < expr`  | less             |
| `expr <= expr` | less or equal    |
| `expr > expr`  | greater          |
| `expr >= expr` | greater or equal |

## Arithmetic operators

| Operator       | Meaning                  |
|----------------|--------------------------|
| `expr + expr`  | addition / concatenation |
| `expr - expr`  | subtraction              |
| `expr * expr`  | multiplication           |
| `expr / expr`  | division                 |
| `expr % expr`  | modulo                   |
| `expr ** expr` | exponentiation           |

## Control structures

### If/Else

The if/else statement looks like this:

```
if condition {
    /* if-statements */
}

if condition {
    /* if-statements */
} else {
    /* else-statements */
}
```

*Note that the condition doesn't require parenthesis.*

### While / For

```
while condition {
    /* while-statements */
}

for declaration; condition; step {
    /* for-statements */
}
```

*There's no `break` or `continue` keywords yet.*

### BuiltIn functions

`print` prints to STDOUT followed by a `\n`.

`printf` prints a formatted string to STDOUT.

`input` gets input from STDIN optionally printing a prompt message.

```
print("This program tells you if your number is even or odd.");

number := input("Number: ") as int;

if number % 2 == 0 {
    printf("Your number %d is even!\n", number);
} else {
    printf("Your number %d is odd!\n", number);
}
```

### Functions

Using the `fn` keyword you can declare your own functions!

```

fn fib(n)
{
    if n <= 1 {
        return n;
    }

    return fib(n - 1) + fib(n - 2);
}

n := fib(10);
printf("Fibonacci of 10 is: %d\n", n);

```
