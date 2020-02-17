# Human-Optimized Config Object Notation

This is a copy of the original [HOCON spec](https://github.com/lightbend/config/blob/master/HOCON.md) adapted for AutoKonfig.

## Goals / Background

The primary goal is: keep the semantics (tree structure; set of
types; encoding/escaping) from JSON, but make it more convenient
as a human-editable config file format.

The following features are desirable, to support human usage:

 - less noisy / less pedantic syntax
 - ability to refer to another part of the configuration (set a value to
   another value)
 - import/include another configuration file into the current file
 - a mapping to a flat properties list such as Java's system properties
 - ability to write comments

Implementation-wise, the format should have these properties:

 - a JSON superset, that is, all valid JSON should be valid and
   should result in the same in-memory data that a JSON parser
   would have produced.
 - be deterministic; the format is flexible, but it is not
   heuristic. It should be clear what's invalid and invalid files
   should generate errors.

HOCON is significantly harder to specify and to parse than
JSON. Think of it as moving the work from the person maintaining
the config file to the computer program.

## Definitions

 - a _key_ is a string JSON would have to the left of `:` and a _value_ is
   anything JSON would have to the right of `:`. i.e. the two
   halves of an object _field_.

 - a _value_ is any "value" as defined in the JSON spec, plus
   unquoted strings and substitutions as defined in this spec.

 - a _field_ is a key, any separator such as ':', and a value.

 - references to a _file_ ("the file being parsed") can be
   understood to mean any byte stream being parsed, not just
   literal files in a filesystem.

## Syntax

As a baseline, all JSON files are valid HOCON files. You can find the
[JSON spec here](https://json.org/).

### Comments

Anything between `//` or `#` and the next newline is considered a comment
and ignored, unless the `//` or `#` is inside a quoted string.

### Omit root braces

JSON documents must have an array or object at the root. Empty
files are invalid documents, as are files containing only a
non-array non-object value such as a string.

In HOCON, if the file does not begin with a square bracket or
curly brace, it is parsed as if it were enclosed with `{}` curly
braces.

A HOCON file is invalid if it omits the opening `{` but still has
a closing `}`; the curly braces must be balanced.

### Key-value separator

The `=` character can be used anywhere JSON allows `:`, i.e. to
separate keys from values.

If a key is followed by `{`, the `:` or `=` may be omitted. So
`"foo" {}` means `"foo" : {}`

### Commas

Fields in objects need not have a comma between them as long as they have at least one newline (`\n`) between them.

Elements in [arrays](/types/#collections) are separated with commas. The last element in an array or last field in an object may be followed by a single comma. This extra comma is ignored. These same comma rules apply to fields in objects.

### Duplicate keys and object merging

The JSON spec does not clarify how duplicate keys in the same
object should be handled. In HOCON, duplicate keys that appear
later override those that appear earlier, unless both values are
objects. If both values are objects, then the objects are merged.

!!! info
    This would make HOCON a non-superset of JSON if you assume that JSON requires duplicate keys to have a behavior. The assumption here is that duplicate keys are invalid JSON.

To merge objects:

 - add fields present in only one of the two objects to the merged
   object.
 - for non-object-valued fields present in both objects,
   the field found in the second object must be used.
 - for object-valued fields present in both objects, the
   object values should be recursively merged according to
   these same rules.

Object merge can be prevented by setting the key to another value
first. This is because merging is always done two values at a
time; if you set a key to an object, a non-object, then an object,
first the non-object falls back to the object (non-object always
wins), and then the object falls back to the non-object (no
merging, object is the new value). So the two objects never see
each other.

These two are equivalent:

``` Lighttpd
{
    "foo" : { "a" : 42 },
    "foo" : { "b" : 43 }
}
```

``` Lighttpd
{
    "foo" : { "a" : 42, "b" : 43 }
}
```

And these two are equivalent:

``` Lighttpd
{
    "foo" : { "a" : 42 },
    "foo" : null,
    "foo" : { "b" : 43 }
}
```

``` Lighttpd
{
    "foo" : { "b" : 43 }
}
```

The intermediate setting of `"foo"` to `null` prevents the object merge.

### Unquoted strings

A sequence of characters outside of a quoted string is a string
value if:

 - it does not contain "forbidden characters": ```$"{}[]:=,+#`^?!@*&\```, or whitespace.
 - it does not contain the two-character string `//` (which
   starts a comment)
 - its initial characters do not parse as `true`, `false`, `null`,
   or a number.

Unquoted strings are used literally, they do not support any kind
of escaping. Quoted strings may always be used as an alternative
when you need to write a character that is not permitted in an
unquoted string.

In general, once an unquoted string begins, it continues until a
forbidden character or the two-character string `//` is
encountered. Embedded (non-initial) booleans, nulls, and numbers
are not recognized as such, they are part of the string.

An unquoted string may not _begin_ with the digits 0-9 or with a
hyphen (`-`) because those are valid characters to begin a
JSON number. The initial number character, plus any valid-in-JSON
number characters that follow it, must be parsed as a number
value. Again, these characters are not special _inside_ an
unquoted string; they only trigger number parsing if they appear
initially.

Note that quoted JSON strings may not contain control characters
(control characters include some whitespace characters, such as
newline). This rule is from the JSON spec. However, unquoted
strings have no restriction on control characters, other than the
ones listed as "forbidden characters" above.

Some of the "forbidden characters" are forbidden because they
already have meaning in JSON or HOCON, others are essentially
reserved keywords to allow future extensions to this spec.

### Multi-line strings

If the three-character sequence `"""` appears, then all
Unicode characters until a closing `"""` sequence are used
unmodified to create a string value. Newlines and whitespace
receive no special treatment. Unicode escapes are not interpreted in triple-quoted
strings.

!!! info
    Any sequence of at least three quotes ends the multi-line string, and any "extra" quotes are part of the string.

### Value concatenation

The value of an object field or array element may consist of
multiple values which are combined. There are three kinds of value
concatenation:

 - if all the values are simple values (neither objects nor
   arrays), they are concatenated into a string.
 - if all the values are arrays, they are concatenated into
   one array.
 - if all the values are objects, they are merged (as with
   duplicate keys) into one object.

String value concatenation is allowed in field keys, in addition
to field values and array elements. Objects and arrays do not make
sense as field keys.

#### String value concatenation

String value concatenation is the trick that makes unquoted
strings work; it also supports substitutions (`${foo}` syntax) in
strings.

As long as simple values are separated only by non-newline
whitespace, the _whitespace between them is preserved_ and the
values, along with the whitespace, are concatenated into a string.

String value concatenations never span a newline, or a character
that is not part of a simple value.

A string value concatenation may appear in any place that a string
may appear, including object keys, object values, and array
elements.

Whenever a value would appear in JSON, a HOCON parser instead
collects multiple values (including the whitespace between them)
and concatenates those values into a string.

Whitespace before the first and after the last simple value must
be discarded. Only whitespace _between_ simple values must be
preserved.

So for example ` foo bar baz ` parses as three unquoted strings,
and the three are value-concatenated into one string. The inner
whitespace is kept and the leading and trailing whitespace is
trimmed. The equivalent string, written in quoted form, would be
`"foo bar baz"`.

Value concatenating `foo bar` (two unquoted strings with
whitespace) and quoted string `"foo bar"` would result in the same
in-memory representation, seven characters.

For purposes of string value concatenation, non-string values are
converted to strings as follows (strings shown as quoted strings):

 - `true` and `false` become the strings `"true"` and `"false"`.
 - `null` becomes the string `"null"`.
 - quoted and unquoted strings are themselves.
 - numbers should be kept as they were originally written in the
   file. For example, if you parse `1e5` then you might render
   it alternatively as `1E5` with capital `E`, or just `100000`.
   For purposes of value concatenation, it should be rendered
   as it was written in the file.
 - a substitution is replaced with its value which is then
   converted to a string as above.
 - it is invalid for arrays or objects to appear in a string value
   concatenation.

#### Array and object concatenation

Arrays can be concatenated with arrays, and objects with objects,
but it is an error if they are mixed.

For purposes of concatenation, "array" also means "substitution
that resolves to an array" and "object" also means "substitution
that resolves to an object."

Within an field value or array element, if only non-newline
whitespace separates the end of a first array or object or
substitution from the start of a second array or object or
substitution, the two values are concatenated. Newlines may occur
_within_ the array or object, but not _between_ them. Newlines
_between_ prevent concatenation.

For objects, "concatenation" means "merging", so the second object
overrides the first.

Arrays and objects cannot be field keys, whether concatenation is
involved or not.

Here are several ways to define `a` to the same object value:

``` Lighttpd
# one object
a : { b : 1, c : 2 }
# two objects that are merged via concatenation rules
a : { b : 1 } { c : 2 }
# two fields that are merged
a : { b : 1 }
a : { c : 2 }
```

Here are several ways to define `a` to the same array value:

``` Lighttpd
# one array
a : [ 1, 2, 3, 4 ]
# two arrays that are concatenated
a : [ 1, 2 ] [ 3, 4 ]
# a later definition referring to an earlier
# (see "self-referential substitutions" below)
a : [ 1, 2 ]
a : ${a} [ 3, 4 ]
```

A common use of object concatenation is "inheritance":

``` Lighttpd
data-center-generic = { cluster-size = 6 }
data-center-east = ${data-center-generic} { name = "east" }
```

A common use of array concatenation is to add to paths:

``` Lighttpd
path = [ /bin ]
path = ${path} [ /usr/bin ]
```

!!! info
    Concatenation with whitespace and substitutions.

    When concatenating substitutions such as `${foo} ${bar}`, the
    substitutions may turn out to be strings (which makes the
    whitespace between them significant) or may turn out to be objects
    or lists (which makes it irrelevant).

#### Arrays without commas or newlines

Arrays allow you to use newlines instead of commas, but not
whitespace instead of commas. Non-newline whitespace will produce
concatenation rather than separate elements.

``` Lighttpd
# this is an array with one element, the string "1 2 3 4"
[ 1 2 3 4 ]
# this is an array of four integers
[ 1
  2
  3
  4 ]
```

If this gets confusing, just use commas. The concatenation
behavior is useful rather than surprising in cases like:

``` Lighttpd
[ This is an unquoted string my name is ${name}, Hello ${world} ]
[ ${a} ${b}, ${x} ${y} ]
```

### Path expressions

Path expressions are used to write out a path through the object
graph. They appear in two places; in substitutions, like
`${foo.bar}`, and as the keys in objects like `{ foo.bar : 42 }`.

When concatenating the path expression, any `.` characters outside
quoted strings are understood as path separators, while inside
quoted strings `.` has no special meaning. So
`foo.bar."hello.world"` would be a path with three elements,
looking up key `foo`, key `bar`, then key `hello.world`.

!!! info
    If a path element is an empty string, it must always be quoted.
    That is, `a."".b` is a valid path with three elements, and the
    middle element is an empty string. But `a..b` is invalid and
    will generate an error. Following the same rule, a path that
    starts or ends with a `.` is invalid and will generate an error.

### Paths as keys

If a key is a path expression with multiple elements, it is
expanded to create an object for each path element other than the
last. The last path element, combined with the value, becomes a
field in the most-nested object.

In other words:

``` Lighttpd
foo.bar : 42
```

is equivalent to:

``` Lighttpd
foo { bar : 42 }
```

and:

``` Lighttpd
foo.bar.baz : 42
```

is equivalent to:

``` Lighttpd
foo { bar { baz : 42 } }
```

and so on. These values are merged in the usual way; which implies
that:

``` Lighttpd
a.x : 42, a.y : 43
```

is equivalent to:

``` Lighttpd
a { x : 42, y : 43 }
```

Because path expressions work like value concatenations, you can
have whitespace in keys:

``` Lighttpd
a b c : 42
```

is equivalent to:

``` Lighttpd
"a b c" : 42
```

Because path expressions are always converted to strings, even
single values that would normally have another type become
strings.

   - `true : 42` is `"true" : 42`
   - `3 : 42` is `"3" : 42`
   - `3.14 : 42` is `"3" : { "14" : 42 }`

As a special rule, the unquoted string `include` may not begin a
path expression in a key, because it has a [special interpretation](#includes).

### Substitutions

Substitutions are a way of referring to other parts of the
configuration tree.

The syntax is `${pathexpression}` or `${?pathexpression}` where
the `pathexpression` is a path expression as described above. This
path expression has the same syntax that you could use for an
object key.

Substitutions are not parsed inside quoted strings. To get a
string containing a substitution, you must use value concatenation
with the substitution in the unquoted portion:

``` Lighttpd
key : ${animal.favorite} is my favorite animal
```

Or you could quote the non-substitution portion:

``` Lighttpd
key : ${animal.favorite}" is my favorite animal"
```

Substitutions are resolved by looking up the path in the
configuration. The path begins with the root configuration object,
i.e. it is "absolute" rather than "relative."

Substitution processing is performed as the last parsing step, so
a substitution can look forward in the configuration. If a
configuration consists of multiple files, it may even end up
retrieving a value from another file.

If a key has been specified more than once, the substitution will
always evaluate to its latest-assigned value (that is, it will
evaluate to the merged object, or the last non-object value that
was set, in the entire document being parsed including all
included files).

If a substitution does not match any value present in the
configuration then it is undefined. An undefined substitution with the `${foo}` syntax is invalid and should generate an error.

If a substitution with the `${?foo}` syntax is undefined:

 - if it is the value of an object field then the field should not
   be created. If the field would have overridden a previously-set
   value for the same field, then the previous value remains.
 - if it is an array element then the element should not be added.
 - if it is part of a value concatenation with another string then
   it should become an empty string; if part of a value
   concatenation with an object or array it should become an empty
   object or array.
 - `foo : ${?bar}` would avoid creating field `foo` if `bar` is
   undefined. `foo : ${?bar}${?baz}` would also avoid creating the
   field if _both_ `bar` and `baz` are undefined.

Substitutions are only allowed in field values and array
elements (value concatenations), they are not allowed in keys or
nested inside other substitutions (path expressions).

#### Self-Referential Substitutions

The big picture:

 - substitutions normally "look forward" and use the final value
   for their path expression
 - when this would create a cycle, when possible the cycle must be
   broken by looking backward only (thus removing one of the
   substitutions that's a link in the cycle)

The idea is to allow a new value for a field to be based on the
older value:

``` Lighttpd
path : "a:b:c"
path : ${path}":d"
```

A _self-referential field_ is one which:

 - has a substitution, or value concatenation containing a
   substitution, as its value
 - where this field value refers to the field being defined,
   either directly or by referring to one or more other
   substitutions which eventually point back to the field being
   defined

Examples of self-referential fields:

 - `a : ${a}`
 - `a : ${a}bc`
 - `path : ${path} [ /usr/bin ]`

Note that an object or array with a substitution inside it is
_not_ considered self-referential for this purpose. The
self-referential rules do _not_ apply to:

 - `a : { b : ${a} }`
 - `a : [${a}]`

These cases are unbreakable cycles that generate an error.

#### The `+=` field separator

Fields may have `+=` as a separator rather than `:` or `=`. A
field with `+=` transforms into a self-referential array
concatenation, like this:

``` Lighttpd
a += b
```

becomes:

``` Lighttpd
a = ${?a} [b]
```

`+=` appends an element to a previous array. If the previous value
was not an array, an error will result just as it would in the
long form `a = ${?a} [b]`. Note that the previous value is
optional (`${?a}` not `${a}`), which allows `a += b` to be the
first mention of `a` in the file (it is not necessary to have `a =
[]` first).

#### Examples of Self-Referential Substitutions

In isolation (with no merges involved), a self-referential field
is an error because the substitution cannot be resolved:

``` Lighttpd
foo : ${foo} # an error
```

When `foo : ${foo}` is merged with an earlier value for `foo`,
however, the substitution can be resolved to that earlier value.
When merging two objects, the self-reference in the overriding
field refers to the overridden field. Say you have:

``` Lighttpd
foo : { a : 1 }
```

and then:

``` Lighttpd
foo : ${foo}
```

Then `${foo}` resolves to `{ a : 1 }`, the value of the overridden
field.

### Includes

#### Include syntax

An _include statement_ consists of the unquoted string `include`
followed by whitespace and then either:

 - a single _quoted_ string which is interpreted heuristically as
   URL, filename, or classpath resource.
 - `url()`, `file()`, or `classpath()` surrounding a quoted string
   which is then interpreted as a URL, file, or classpath. The
   string must be quoted.
 - `required()` surrounding one of the above

An include statement can appear in place of an object field.

Value concatenation is _not_ performed on the "argument" to
`include` or `url()` etc. The argument must be a single quoted
string. No substitutions are allowed, and the argument may not be
an unquoted string or any other kind of value.

You can quote `"include"` if you want a key that starts with the
word `"include"`, only unquoted `include` is special:

``` Lighttpd
{ "include" : 42 }
```

#### Include semantics: merging

An _including file_ contains the include statement and an
_included file_ is the one specified in the include statement.
(They need not be regular files on a filesystem, but assume they
are for the moment.)

An included file must contain an object, not an array. This is
significant because both JSON and HOCON allow arrays as root
values in a document.

If an included file contains an array as the root value, it is
invalid and an error should be generated.

The included file should be parsed, producing a root object. The
keys from the root object are conceptually substituted for the
include statement in the including file.

 - If a key in the included object occurred prior to the include
   statement in the including object, the included key's value
   overrides or merges with the earlier value, exactly as with
   duplicate keys found in a single file.
 - If the including file repeats a key from an earlier-included
   object, the including file's value would override or merge
   with the one from the included file.

#### Include semantics: substitution

Substitutions in included files are looked up at two different
paths; first, relative to the root of the included file; second,
relative to the root of the including configuration.

#### Include semantics: missing files and required files

By default, if an included file does not exist then the include statement should
be silently ignored (as if the included file contained only an
empty object).

If however an included resource is mandatory then the name of the
included resource may be wrapped with `required()`, in which case
file parsing will fail with an error if the resource cannot be resolved.

The syntax for this is

``` Lighttpd
include required("foo.conf")
include required(file("foo.conf"))
include required(classpath("foo.conf"))
include required(url("http://localhost/foo.conf"))
```

#### Include semantics: locating resources

A quoted string not surrounded by `url()`, `file()`, `classpath()`
must be interpreted heuristically. The heuristic is to treat the
quoted string as:

 - a URL, if the quoted string is a valid URL with a known
   protocol.
 - otherwise, a file.
 - if an include statement does not identify a valid URL or an existing file it will fall back to a classpath resource.  This allows configurations found in files
   or URLs to access classpath resources in a natural way.

For resources located on the Java classpath:

 - included resources are looked up by calling `getResource()` on
   the same class loader used to look up the including resource.
 - if the included resource name is absolute (starts with '/')
   then it will be passed to `getResource()` with the '/'
   removed.
 - if the included resource name does not start with '/' then it
   should have the "directory" of the including resource
   prepended to it, before passing it to `getResource()`.  If the
   including resource is not absolute (no '/') and has no "parent
   directory" (is just a single path element), then the included
   relative resource name should be left as-is.
 - it would be wrong to use `getResource()` to get a URL and then
   locate the included name relative to that URL, because a class
   loader is not required to have a one-to-one mapping between
   paths in its URLs and the paths it handles in `getResource()`.
   In other words, the "adjacent to" computation will be done
   on the resource name not on the resource's URL.

For plain files on the filesystem:

 - if the included file is an absolute path then it should be kept
   absolute and loaded as such.
 - if the included file is a relative path, then it should be
   located relative to the directory containing the including
   file.
 - if the file is not found, fall back to the classpath resource.
   The classpath resource should not have any package name added
   in front, it should be relative to the "root"; which means any
   leading "/" should just be removed (absolute is the same as
   relative since it's root-relative). The "/" is handled for
   consistency with including resources from inside other
   classpath resources, where the resource name may not be
   root-relative and "/" allows specifying relative to root.

URLs:

 - for files loaded from a URL, "adjacent to" should be based
   on parsing the URL's path component, replacing the last
   path element with the included name.
 - file: URLs should behave in exactly the same way as a plain
   filename

Note that at present, if `url()`/`file()`/`classpath()` are
specified, the included items are NOT interpreted relative to the
including items. Relative-to-including-file paths only work with
the heuristic `include "foo.conf"`. This may change in the future.
