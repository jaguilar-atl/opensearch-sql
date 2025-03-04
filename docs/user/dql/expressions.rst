===========
Expressions
===========

.. rubric:: Table of contents

.. contents::
   :local:
   :depth: 3


Introduction
============

Expressions, particularly value expressions, are those which return a scalar value. Expressions have different types and forms. For example, there are literal values as atom expression and arithmetic, predicate and function expression built on top of them. And also expressions can be used in different clauses, such as using arithmetic expression in ``SELECT``, ``WHERE`` or ``HAVING`` clause.

Literal Values
==============

Description
-----------

A literal is a symbol that represents a value. The most common literal values include:

1. Numeric literals: specify numeric values such as integer and floating-point numbers.
2. String literals: specify a string enclosed by single or double quotes.
3. Boolean literals: ``true`` or ``false``.
4. Date and Time literals: DATE 'YYYY-MM-DD' represent the date, TIME 'hh:mm:ss' represent the time, TIMESTAMP 'YYYY-MM-DD hh:mm:ss' represent the timestamp. You can also surround the literals with curly brackets, if you do, you can replace date with d, time with t, and timestamp with ts

Examples
--------

Here is an example for different type of literals::

    os> SELECT 123, 'hello', false, -4.567, DATE '2020-07-07', TIME '01:01:01', TIMESTAMP '2020-07-07 01:01:01';
    fetched rows / total rows = 1/1
    +-------+-----------+---------+----------+---------------------+-------------------+-----------------------------------+
    | 123   | 'hello'   | false   | -4.567   | DATE '2020-07-07'   | TIME '01:01:01'   | TIMESTAMP '2020-07-07 01:01:01'   |
    |-------+-----------+---------+----------+---------------------+-------------------+-----------------------------------|
    | 123   | hello     | False   | -4.567   | 2020-07-07          | 01:01:01          | 2020-07-07 01:01:01               |
    +-------+-----------+---------+----------+---------------------+-------------------+-----------------------------------+


    os> SELECT "Hello", 'Hello', "It""s", 'It''s', "It's", '"Its"', 'It\'s', 'It\\\'s', "\I\t\s"
    fetched rows / total rows = 1/1
    +-----------+-----------+-----------+-----------+----------+-----------+-----------+-------------+------------+
    | "Hello"   | 'Hello'   | "It""s"   | 'It''s'   | "It's"   | '"Its"'   | 'It\'s'   | 'It\\\'s'   | "\I\t\s"   |
    |-----------+-----------+-----------+-----------+----------+-----------+-----------+-------------+------------|
    | Hello     | Hello     | It"s      | It's      | It's     | "Its"     | It's      | It\'s       | \I\t\s     |
    +-----------+-----------+-----------+-----------+----------+-----------+-----------+-------------+------------+


    os> SELECT {DATE '2020-07-07'}, {D '2020-07-07'}, {TIME '01:01:01'}, {T '01:01:01'}, {TIMESTAMP '2020-07-07 01:01:01'}, {TS '2020-07-07 01:01:01'}
    fetched rows / total rows = 1/1
    +-----------------------+--------------------+---------------------+------------------+-------------------------------------+------------------------------+
    | {DATE '2020-07-07'}   | {D '2020-07-07'}   | {TIME '01:01:01'}   | {T '01:01:01'}   | {TIMESTAMP '2020-07-07 01:01:01'}   | {TS '2020-07-07 01:01:01'}   |
    |-----------------------+--------------------+---------------------+------------------+-------------------------------------+------------------------------|
    | 2020-07-07            | 2020-07-07         | 01:01:01            | 01:01:01         | 2020-07-07 01:01:01                 | 2020-07-07 01:01:01          |
    +-----------------------+--------------------+---------------------+------------------+-------------------------------------+------------------------------+

Limitations
-----------

The current implementation has the following limitations at the moment:

1. Only literals of data types listed as above are supported for now. Other type of literals, such as NULL, will be added in future.
2. Expression of literals, such as arithmetic expressions, will be supported later.
3. Standard ANSI ``VALUES`` clause is not supported, although the ``SELECT`` literal example above is implemented by a Values operator internally.
4. Date and Time literals only support DATE_FORMAT listed above.

Arithmetic Expressions
======================

Description
-----------

Operators
`````````

Arithmetic expression is an expression formed by numeric literals and binary arithmetic operators as follows:

1. ``+``: Add.
2. ``-``: Subtract.
3. ``*``: Multiply.
4. ``/``: Divide. For integers, the result is an integer with fractional part discarded.
5. ``%``: Modulo. This can be used with integers only with remainder of the division as result.

Precedence
``````````

Parentheses can be used to control the precedence of arithmetic operators. Otherwise, operators of higher precedence is performed first.

Type Conversion
```````````````

Implicit type conversion is performed when looking up operator signature. For example, an integer ``+`` a real number matches signature ``+(double,double)`` which results in a real number. This rule also applies to function call discussed below.

Examples
--------

Here is an example for different type of arithmetic expressions::

    os> SELECT 1 + 2, (9 - 1) % 3, 2 * 4 / 3;
    fetched rows / total rows = 1/1
    +---------+---------------+-------------+
    | 1 + 2   | (9 - 1) % 3   | 2 * 4 / 3   |
    |---------+---------------+-------------|
    | 3       | 2             | 2           |
    +---------+---------------+-------------+

Comparison Operators
==================================

Description
-----------

Comparison operators are used to compare values. The MISSING and NULL value comparison has following the rule. MISSING value only equal to MISSING value and less than all the other values. NULL value equals to NULL value, large than MISSING value, but less than all the other values.

Operators
`````````

+----------------+----------------------------------------+
| name           | description                            |
+----------------+----------------------------------------+
| >              | Greater than operator                  |
+----------------+----------------------------------------+
| >=             | Greater than or equal operator         |
+----------------+----------------------------------------+
| <              | Less than operator                     |
+----------------+----------------------------------------+
| !=             | Not equal operator                     |
+----------------+----------------------------------------+
| <=             | Less than or equal operator            |
+----------------+----------------------------------------+
| =              | Equal operator                         |
+----------------+----------------------------------------+
| LIKE           | Simple Pattern matching                |
+----------------+----------------------------------------+
| IS NULL        | NULL value test                        |
+----------------+----------------------------------------+
| IS NOT NULL    | NOT NULL value test                    |
+----------------+----------------------------------------+
| IS MISSING     | MISSING value test                     |
+----------------+----------------------------------------+
| IS NOT MISSING | NOT MISSING value test                 |
+----------------+----------------------------------------+
| REGEXP         | String matches regular expression test |
+----------------+----------------------------------------+
| IN             | IN value list test                     |
+----------------+----------------------------------------+
| NOT IN         | NOT IN value list test                 |
+----------------+----------------------------------------+
| BETWEEN        | Between a range (endpoint inclusive)   |
+----------------+----------------------------------------+
| NOT BETWEEN    | Not between a range (BETWEEN negation) |
+----------------+----------------------------------------+

Basic Comparison Operator
-------------------------

Here is an example for different type of comparison operators::

    os> SELECT 2 > 1, 2 >= 1, 2 < 1, 2 != 1, 2 <= 1, 2 = 1;
    fetched rows / total rows = 1/1
    +---------+----------+---------+----------+----------+---------+
    | 2 > 1   | 2 >= 1   | 2 < 1   | 2 != 1   | 2 <= 1   | 2 = 1   |
    |---------+----------+---------+----------+----------+---------|
    | True    | True     | False   | True     | False    | False   |
    +---------+----------+---------+----------+----------+---------+

It is possible to compare datetimes. When comparing different datetime types, for example `DATE` and `TIME`, both converted to `DATETIME`.
The following rule is applied on coversion: a `TIME` applied to today's date; `DATE` is interpreted at midnight. See example below::

    os> SELECT current_time() > current_date() AS `now.time > today`, typeof(current_time()) AS `now.time.type`, typeof(current_date()) AS `now.date.type`;
    fetched rows / total rows = 1/1
    +--------------------+-----------------+-----------------+
    | now.time > today   | now.time.type   | now.date.type   |
    |--------------------+-----------------+-----------------|
    | True               | TIME            | DATE            |
    +--------------------+-----------------+-----------------+

    os> SELECT current_time() = now() AS `now.time = now`, typeof(current_time()) AS `now.time.type`, typeof(now()) AS `now.type`;
    fetched rows / total rows = 1/1
    +------------------+-----------------+------------+
    | now.time = now   | now.time.type   | now.type   |
    |------------------+-----------------+------------|
    | True             | TIME            | DATETIME   |
    +------------------+-----------------+------------+

    os> SELECT subtime(now(), current_time()) = current_date() AS `midnight = now.date`, typeof(subtime(now(), current_time())) AS `midnight.type`, typeof(current_date()) AS `now.date.type`;
    fetched rows / total rows = 1/1
    +-----------------------+-----------------+-----------------+
    | midnight = now.date   | midnight.type   | now.date.type   |
    |-----------------------+-----------------+-----------------|
    | True                  | DATETIME        | DATE            |
    +-----------------------+-----------------+-----------------+


LIKE
----

expr LIKE pattern. The expr is string value, pattern is supports literal text, a percent ( % ) character for a wildcard, and an underscore ( _ ) character for a single character match, pattern is case insensitive::

    os> SELECT 'axyzb' LIKE 'a%b', 'acb' LIKE 'A_B', 'axyzb' NOT LIKE 'a%b', 'acb' NOT LIKE 'a_b';
    fetched rows / total rows = 1/1
    +----------------------+--------------------+--------------------------+------------------------+
    | 'axyzb' LIKE 'a%b'   | 'acb' LIKE 'A_B'   | 'axyzb' NOT LIKE 'a%b'   | 'acb' NOT LIKE 'a_b'   |
    |----------------------+--------------------+--------------------------+------------------------|
    | True                 | True               | False                    | False                  |
    +----------------------+--------------------+--------------------------+------------------------+

NULL value test
---------------

Here is an example for null value test::

    os> SELECT 0 IS NULL, 0 IS NOT NULL, NULL IS NULL, NULL IS NOT NULL;
    fetched rows / total rows = 1/1
    +-------------+-----------------+----------------+--------------------+
    | 0 IS NULL   | 0 IS NOT NULL   | NULL IS NULL   | NULL IS NOT NULL   |
    |-------------+-----------------+----------------+--------------------|
    | False       | True            | True           | False              |
    +-------------+-----------------+----------------+--------------------+


REGEXP value test
-----------------

expr REGEXP pattern. The expr is string value, pattern is supports regular expression patterns::

    os> SELECT 'Hello!' REGEXP '.*', 'a' REGEXP 'b';
    fetched rows / total rows = 1/1
    +------------------------+------------------+
    | 'Hello!' REGEXP '.*'   | 'a' REGEXP 'b'   |
    |------------------------+------------------|
    | 1                      | 0                |
    +------------------------+------------------+

IN value list test
------------------

Here is an example for IN value test::

    os> SELECT 1 in (1, 2), 3 not in (1, 2);
    fetched rows / total rows = 1/1
    +---------------+-------------------+
    | 1 in (1, 2)   | 3 not in (1, 2)   |
    |---------------+-------------------|
    | True          | True              |
    +---------------+-------------------+

BETWEEN range test
------------------

Here is an example for range test by BETWEEN expression::

    os> SELECT
    ...  1 BETWEEN 1 AND 3,
    ...  4 BETWEEN 1 AND 3,
    ...  4 NOT BETWEEN 1 AND 3;
    fetched rows / total rows = 1/1
    +---------------------+---------------------+-------------------------+
    | 1 BETWEEN 1 AND 3   | 4 BETWEEN 1 AND 3   | 4 NOT BETWEEN 1 AND 3   |
    |---------------------+---------------------+-------------------------|
    | True                | False               | True                    |
    +---------------------+---------------------+-------------------------+


Function Call
=============

Description
-----------

A function call is declared by function name followed by its arguments. The arguments are enclosed in parentheses and separated by comma. For complete function list supported, please see also: `SQL Functions <functions.rst>`_

Syntax
``````

A typical function call is in the following form::

 function_name ( [ expression [, expression]* ]? )

Null Handling
`````````````

If any argument is missing or null, the final result of evaluation will be missing or null accordingly.

Arithmetic function examples
----------------------------

Here is an example for different type of arithmetic expressions::

    os> SELECT abs(-1.234), abs(-1 * abs(-5));
    fetched rows / total rows = 1/1
    +---------------+---------------------+
    | abs(-1.234)   | abs(-1 * abs(-5))   |
    |---------------+---------------------|
    | 1.234         | 5                   |
    +---------------+---------------------+

Date function examples
----------------------

Here is an example for different type of arithmetic expressions::

    os> SELECT dayofmonth(DATE '2020-07-07');
    fetched rows / total rows = 1/1
    +---------------------------------+
    | dayofmonth(DATE '2020-07-07')   |
    |---------------------------------|
    | 7                               |
    +---------------------------------+

Limitations
-----------

1. Only a subset of the SQL functions above is implemented in new engine for now. More function support are being added.
2. For now function name is required to be lowercase.

