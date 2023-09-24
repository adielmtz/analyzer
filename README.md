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
my_var := "Hello";
```

The interpreter will throw a fatal error if you attempt to re-declare a variable within the same scope.

```
my_var := "Again!"; // Fatal Error: 'my_var' is already defined.
```

However, you can declare a variable with the same identifier (name) within a nested scope.

```
my_var := "Outer";

{
    // This is allowed, as the inner-most variable
    // shadows the variable in the parent scope.
    my_var := "Inner";
    println my_var;
}

println my_var;
```

### Using a variable

Variables can be assigned a value with the `=` operator.

```
my_var = "Hello there";
println my_var;
```

And the interpreter will throw a fatal error if the variable is undefined.

```
unknown = "???"; // Fatal Error: undefined variable 'unknown'.
```

Variables can store any type of value at any moment:

```
my_var = [1, 2, 3];
my_var = true;
my_var = 3.1415;
my_var = 2023;
my_var = "A113";
```

The `is` operator checks if the given value is of a specific type, while the `as` operator
casts the value from one type to another:

```
// Check if the value is an string
if my_var is string {

    // Cast value to integer
    my_int := my_var as int;

}
```

Use the `delete` operator to _delete_ a variable:

```
delete my_var;
println my_var; // Fatal Error: undefined variable 'my_var'.
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
println arr[0];
```

And with the `delete` operator, you can delete one value from the array (the array will be re-indexed):

```
arr := ["a", "b", "c"];
println arr[0]; // "a"
delete arr[0];
println arr[0]; // "b"
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

### Print / Println

`print` prints a list of expressions to the standard output.
`println` prints a single expression to the standard output, followed by a line break.

```
print "a", "b", "c"; // output: abc
println "Hello!";    // output: "Hello!\n"

// These two statements are equivalent:
print "Waah\n";
println "Waah";
```
