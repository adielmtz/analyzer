# Analyzer

Parser & interpreter for a custom language.
This project was made for study and research about compiler construction.

## Types

The language supports these 4 primitive types:

1. `bool` (java.lang.Boolean).
2. `float` (java.lang.Double).
3. `integer` (java.lang.Long).
4. `string` (java.lang.String).

(Arrays not supported _yet_)

Numeric literals can be expressed in different ways:

* Binary: `0b100101`.
* Hexadecimal: `0xffa500`.
* Octal: `0o27`.
* Scientific: `1.30e5`.

## Comments

```
// Line comment

/*
    Block comment
 */
```

## Variables

### Declaring a variable

The `:=` operator (borrowed from Go) can be used to declare a new variable:

```
my_bool   := true;
my_float  := 3.1415;
my_int    := 1;
my_string := "Waah";
```

The engine will throw a fatal error if the variable already exists:

```
my_bool := false; // Fatal Error: 'my_bool' is already defined.
```

### Using a variable

The usual behaviour from most programming languages:

```
my_var = true;
my_var = "A113";
my_var = 2023;
```

The engine will throw a fatal error if the variable is undefined:

```
unknown = "?"; // Fatal Error: undefined variable 'unknown'.
```

## Logic operators

The language supports the common logic operators:

* `&&` and.
* `||` or.
* `==` equal.
* `!=` not equal.
* `<` smaller.
* `<=` smaller or equal.
* `>` greater.
* `>=` greater or equal.

## Arithmetic operators

* `+` add / string concatenation.
* `-` subtract.
* `*` multiply.
* `**` power.
* `/` division.
* `%` modulus.

## Expressions

All expressions must be terminated with a semicolon `;`.

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

### Unset

Removes a variable from the program.

```
var := "It's alive!"; // Declare a variable

print var; // Ok.
unset var;
print var; // Fatal Error: undefined variable 'var'. 
```
