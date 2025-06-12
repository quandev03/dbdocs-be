grammar DBML;

// Root rule
dbml: statement* EOF;

statement
    : tableDeclaration
    | refDeclaration
    | enumDeclaration
    | tableGroupDeclaration
    | projectDeclaration
    | comment
    | NEWLINE
    ;

// Project
projectDeclaration: 'Project' projectName '{' NEWLINE* projectElement* '}' NEWLINE*;
projectName: STRING | IDENTIFIER;
projectElement
    : comment
    | 'database_type:' databaseType NEWLINE
    | 'note:' note NEWLINE
    ;
databaseType: STRING | IDENTIFIER;
note: STRING | IDENTIFIER;

// Table
tableDeclaration: tableKeyword tableName tableAlias? '{' NEWLINE* tableElement* '}' NEWLINE*;
tableKeyword: 'Table' | 'table';
tableName: STRING | IDENTIFIER | QUOTED_IDENTIFIER;
tableAlias: 'as' IDENTIFIER;
tableElement
    : columnDeclaration
    | indexDeclaration
    | tableNoteDeclaration
    | comment
    | NEWLINE
    ;

// Column
columnDeclaration: columnName dataType columnSettings? NEWLINE;
columnName: STRING | IDENTIFIER | QUOTED_IDENTIFIER;
dataType: IDENTIFIER ('(' columnTypeParam ')')?;
columnTypeParam: NUMBER | STRING | IDENTIFIER;
columnSettings: '[' columnSetting (',' columnSetting)* ']';
columnSetting
    : 'pk'
    | 'primary key'
    | 'unique'
    | 'not null'
    | 'null'
    | 'increment'
    | 'note:' STRING
    | 'default:' defaultValue
    | 'ref:' refValue
    ;
defaultValue: NUMBER | STRING | BOOLEAN | 'null';
refValue: (tableName '.')? columnName refCardinality?;
refCardinality: '>' | '<' | '-';

// Table note
tableNoteDeclaration: 'note:' STRING NEWLINE;

// Index
indexDeclaration: 'indexes' '{' NEWLINE* indexElement* '}' NEWLINE*;
indexElement
    : indexColumnsDeclaration
    | comment
    | NEWLINE
    ;
indexColumnsDeclaration: '(' indexColumnName (',' indexColumnName)* ')' indexSettings? NEWLINE;
indexColumnName: columnName (indexColumnNameOption)?;
indexColumnNameOption: 'asc' | 'desc';
indexSettings: '[' indexSetting (',' indexSetting)* ']';
indexSetting
    : 'name:' STRING
    | 'unique'
    | 'type:' STRING
    | 'note:' STRING
    ;

// Ref
refDeclaration: 'Ref' refName? ':' refValue refCardinality refValue NEWLINE;
refName: STRING | IDENTIFIER;

// Enum
enumDeclaration: 'Enum' enumName '{' NEWLINE* enumElement* '}' NEWLINE*;
enumName: STRING | IDENTIFIER;
enumElement
    : enumValue
    | comment
    | NEWLINE
    ;
enumValue: IDENTIFIER enumSettings? NEWLINE;
enumSettings: '[' enumSetting (',' enumSetting)* ']';
enumSetting: 'note:' STRING;

// TableGroup
tableGroupDeclaration: 'TableGroup' tableGroupName '{' NEWLINE* tableGroupElement* '}' NEWLINE*;
tableGroupName: STRING | IDENTIFIER;
tableGroupElement
    : tableName
    | comment
    | NEWLINE
    ;

// Comment
comment: COMMENT NEWLINE;

// Basic tokens
BOOLEAN: 'true' | 'false';
NUMBER: '-'? [0-9]+ ('.' [0-9]+)?;
IDENTIFIER: [a-zA-Z_] [a-zA-Z0-9_]*;
QUOTED_IDENTIFIER: '`' ~('`')* '`';
STRING: '\'' ( ~('\'' | '\r' | '\n') | '\'\'' )* '\'' | '"' ( ~('"' | '\r' | '\n') | '""' )* '"';
COMMENT: '//' ~[\r\n]*;
NEWLINE: [\r\n]+;
WS: [ \t]+ -> skip; 