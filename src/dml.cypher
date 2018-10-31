// Inserting data

// Inserting VinculoUsp
LOAD CSV WITH HEADERS FROM "file:///csv/vinculo_usp.csv" AS row
CREATE (n:VinculoUsp)
SET n = row,
	n.NROUSP = toInteger(row.NROUSP), n.TIPOVINC = toInteger(row.TIPOVINC),
    n.NOME = row.NOME, n.DATAINGRESSO =  date(row.DATAINGRESSO), n.DATANASCIMENTO = date(row.DATANASCIMENTO),
    n.ATIVO = row.ATIVO

// Inserting Aluno
LOAD CSV WITH HEADERS FROM "file:///csv/aluno.csv" AS row
CREATE (n:Aluno)
SET n = row,
	n.NROUSP = toInteger(row.NROUSP), n.IDADE = toInteger(row.IDADE),
    n.CIDADENASC = row.CIDADENASC, n.ESTADO = row.ESTADO

// Inserting Funcionario

LOAD CSV WITH HEADERS FROM "file:///csv/funcionario.csv" AS row
CREATE (n:Funcionario)
SET n = row,
	n.NROUSP = toInteger(row.NROUSP), n.NIVEL = row.NIVEL,
    n.LOTACAO = row.LOTACAO

// Inserting Professor
LOAD CSV WITH HEADERS FROM "file:///csv/professor.csv" AS row
CREATE (n:Professor)
SET n = row,
	n.NROUSP = toInteger(row.NROUSP), n.TITULACAO = row.TITULACAO,
    n.PREDIO = toInteger(row.PREDIO), n.SALA = toInteger(row.SALA)

// Inserting Disciplina
LOAD CSV WITH HEADERS FROM "file:///csv/disciplina.csv" AS row
CREATE (n:Disciplina)
SET n = row,
	n.COD = row.COD, n.NOME = row.NOME,
    n.EMENTA = row.EMENTA

// Inserting Leciona JUS TO CREATE A RELATION BETWEEN Professor and Disciplina
LOAD CSV WITH HEADERS FROM "file:///csv/leciona.csv" AS row
CREATE (n:Leciona)
SET n = row,
	n.NROUSPPROF = toInteger(row.NROUSPPROF), n.CODDISC = row.CODDISC

// Inserting Turma
LOAD CSV WITH HEADERS FROM "file:///csv/turma.csv" AS row
CREATE (n:Turma)
SET n = row,
	n.NROUSPPROF = toInteger(row.NROUSPPROF), n.CODDISC = row.CODDISC,
    n.NROTURMA = toInteger(row.NROTURMA), n.ANO = toInteger(row.ANO),
    n.NROALUNOS = toInteger(row.NROALUNOS)


// Inserting Matricula
LOAD CSV WITH HEADERS FROM "file:///csv/matricula.csv" AS row
CREATE (n:Matricula)
SET n = row,
	n.NROUSP = toInteger(row.NROUSP), n.CODDISC = row.CODDISC,
    n.ANO = toInteger(row.ANO), n.NROTURMA =  toInteger(row.NROTURMA),
    n.NOTA = toFloat(row.NOTA)

// Inserting CursoEletronico
LOAD CSV WITH HEADERS FROM "file:///csv/curso_eletronico.csv" AS row
CREATE (n:CursoEletronico)
SET n = row,
	n.CODCURSO = row.CODCURSO, n.NROTURMA = toInteger(row.NROTURMA),
    n.ANO = toInteger(row.ANO), n.CODDISC = row.CODDISC,
    n.ACEITAEXTERNOS = row.ACEITAEXTERNOS 

// Nao tratei para aceitar apenas y ou n no campo aceita externos
// pois o neo4j não suporta

// Inserting Participante
LOAD CSV WITH HEADERS FROM "file:///csv/participante.csv" AS row
CREATE (n:Participante)
SET n = row,
	n.CODCURSO = row.CODCURSO, n.NROUSP = toInteger(row.NROUSP),
    n.NOTA = toFloat(row.NOTA), n.NOTAFINAL = toFloat(row.NOTAFINAL)

// Inserting Grupo
LOAD CSV WITH HEADERS FROM "file:///csv/grupo.csv" AS row
CREATE (n:Grupo)
SET n = row,
	n.CODCURSO = row.CODCURSO, n.NROGRUPO = toInteger(row.NROGRUPO),
    n.NOMEGRUPO = row.NOMEGRUPO

// Inserting CompoeGrupo
LOAD CSV WITH HEADERS FROM "file:///csv/compoe_grupo.csv" AS row
CREATE (n:CompoeGrupo)
SET n = row,
	n.CODCURSO = row.CODCURSO, n.NROGRUPO = toInteger(row.NROGRUPO),
    n.NROUSP = row.NROUSP

//*************************************************************************************************************//

// Deleting Nodes

// Removing Leciona
MATCH (L:Leciona) DETACH DELETE L

// Removing all
MATCH (n) DETACH DELETE n

