// Inserting data

// Inserting VinculoUsp
LOAD CSV WITH HEADERS FROM "file:///csv/vinculo_usp.csv" AS row
CREATE (n:VinculoUsp)
SET n = row,
	n.NROUSP = row.NROUSP, n.TIPOVINC = row.TIPOVINC,
    n.NOME = row.NOME, n.DATAINGRESSO =  row.DATAINGRESSO, n.DATANASCIMENTO = row.DATANASCIMENTO,
    n.ATIVO = row.ATIVO;

// Inserting Aluno
LOAD CSV WITH HEADERS FROM "file:///csv/aluno.csv" AS row
CREATE (n:Aluno)
SET n = row,
	n.NROUSP = row.NROUSP, n.IDADE = row.IDADE,
    n.CIDADENASC = row.CIDADENASC, n.ESTADO = row.ESTADO;

// Inserting Funcionario

LOAD CSV WITH HEADERS FROM "file:///csv/funcionario.csv" AS row
CREATE (n:Funcionario)
SET n = row,
	n.NROUSP = row.NROUSP, n.NIVEL = row.NIVEL,
    n.LOTACAO = row.LOTACAO;

// Inserting Professor
LOAD CSV WITH HEADERS FROM "file:///csv/professor.csv" AS row
CREATE (n:Professor)
SET n = row,
	n.NROUSP = row.NROUSP, n.TITULACAO = row.TITULACAO,
    n.PREDIO = row.PREDIO, n.SALA = row.SALA;

// Inserting Disciplina
LOAD CSV WITH HEADERS FROM "file:///csv/disciplina.csv" AS row
CREATE (n:Disciplina)
SET n = row,
	n.COD = row.COD, n.NOME = row.NOME,
    n.EMENTA = row.EMENTA;

// Inserting Leciona JUS TO CREATE A RELATION BETWEEN Professor and Disciplina
LOAD CSV WITH HEADERS FROM "file:///csv/leciona.csv" AS row
CREATE (n:Leciona)
SET n = row,
	n.NROUSPPROF = row.NROUSPPROF, n.CODDISC = row.CODDISC;

// Inserting Turma
LOAD CSV WITH HEADERS FROM "file:///csv/turma.csv" AS row
CREATE (n:Turma)
SET n = row,
	n.NROUSPPROF = row.NROUSPPROF, n.CODDISC = row.CODDISC,
    n.NROTURMA = row.NROTURMA, n.ANO = row.ANO,
    n.NROALUNOS = row.NROALUNOS;


// Inserting Matricula
LOAD CSV WITH HEADERS FROM "file:///csv/matricula.csv" AS row
CREATE (n:Matricula)
SET n = row,
	n.NROUSP = row.NROUSP, n.CODDISC = row.CODDISC,
    n.ANO = row.ANO, n.NROTURMA =  row.NROTURMA,
    n.NOTA = row.NOTA;

// Inserting CursoEletronico
LOAD CSV WITH HEADERS FROM "file:///csv/curso_eletronico.csv" AS row
CREATE (n:CursoEletronico)
SET n = row,
	n.CODCURSO = row.CODCURSO, n.NROTURMA = row.NROTURMA,
    n.ANO = row.ANO, n.CODDISC = row.CODDISC,
    n.ACEITAEXTERNOS = row.ACEITAEXTERNOS;

// Nao tratei para aceitar apenas y ou n no campo aceita externos
// pois o neo4j não suporta

// Inserting Participante
LOAD CSV WITH HEADERS FROM "file:///csv/participante.csv" AS row
CREATE (n:Participante)
SET n = row,
	n.CODCURSO = row.CODCURSO, n.NROUSP = row.NROUSP,
    n.NOTA = row.NOTA, n.NOTAFINAL = row.NOTAFINAL;

// Inserting Grupo
LOAD CSV WITH HEADERS FROM "file:///csv/grupo.csv" AS row
CREATE (n:Grupo)
SET n = row,
	n.CODCURSO = row.CODCURSO, n.NROGRUPO = row.NROGRUPO,
    n.NOMEGRUPO = row.NOMEGRUPO;

// Inserting CompoeGrupo
LOAD CSV WITH HEADERS FROM "file:///csv/compoe_grupo.csv" AS row
CREATE (n:CompoeGrupo)
SET n = row,
	n.CODCURSO = row.CODCURSO, n.NROGRUPO = row.NROGRUPO,
    n.NROUSP = row.NROUSP;

//*************************************************************************************************************//

// Deleting Nodes

// Removing Leciona
MATCH (L:Leciona) DETACH DELETE L;

// Removing CompoeGrupo
MATCH (L:CompoeGrupo) DETACH DELETE L;

// Removing all
MATCH (n) DETACH DELETE n;

