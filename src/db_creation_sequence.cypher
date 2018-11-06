CREATE CONSTRAINT ON (V:VinculoUsp) ASSERT V.NROUSP IS UNIQUE;
CREATE CONSTRAINT ON (A:Aluno) ASSERT A.NROUSP IS UNIQUE;
CREATE CONSTRAINT ON (F:Funcionario) ASSERT F.NROUSP IS UNIQUE;
CREATE CONSTRAINT ON (P:Professor) ASSERT P.NROUSP IS UNIQUE;
CREATE CONSTRAINT ON (D:Disciplina) ASSERT D.Disciplina IS UNIQUE;
CREATE CONSTRAINT ON (T:Turma) ASSERT (T.NROTURMA, T.CODDISC, T.ANO) IS NODE KEY;
CREATE CONSTRAINT ON (M:Matricula) ASSERT (M.NROUSP, M.CODDISC, M.ANO) IS NODE KEY;
CREATE CONSTRAINT ON (C:CursoEletronico) ASSERT (C.CODCURSO) IS UNIQUE;
CREATE CONSTRAINT ON (C:CursoEletronico) ASSERT (C.NROTURMA, C.CODDISC) IS NODE KEY;
CREATE CONSTRAINT ON (P:Participante) ASSERT (P.CODCURSO, P.NROUSP) IS NODE KEY;
CREATE CONSTRAINT ON (G:Grupo) ASSERT (G.NROGRUPO, G.CODCURSO) IS NODE KEY;
CREATE CONSTRAINT ON (CG:CompoeGrupo) ASSERT (CG.CODCURSO, CG.NROUSP) IS NODE KEY;

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

// Creating relations //

// Aluno - VinculoUsp
MATCH (V:VinculoUsp),(A:Aluno) WHERE V.NROUSP = A.NROUSP
CREATE (A)-[:Has]->(V);

// Funcionario - VinculoUsp
MATCH (V:VinculoUsp),(F:Funcionario) WHERE V.NROUSP = F.NROUSP
CREATE (F)-[:Has]->(V);

// Professor - VinculoUsp
MATCH (V:VinculoUsp),(P:Professor) WHERE V.NROUSP = P.NROUSP
CREATE (P)-[:Has]->(V);

// Disciplina - Turma
MATCH (D:Disciplina), (T:Turma) WHERE D.COD = T.CODDISC
CREATE (D)<-[:HasClass]-(T);

// Professor - Turma
MATCH  (P:Professor), (T:Turma) WHERE P.NROUSP = T.NROUSPPROF
CREATE (P)-[:Teaches]->(T);

// Vinculo - Matricula - Turma
MATCH (V:VinculoUsp), (T:Turma), (M:Matricula) WHERE V.NROUSP = M.NROUSP AND T.CODDISC = M.CODDISC
CREATE (V)-[:Has]->(M)-[:Enrolls]->(T);

// Turma - CursoEletronico
MATCH (T:Turma), (C:CursoEletronico) WHERE T.NROTURMA = C.NROTURMA AND T.CODDISC = C.CODDISC AND T.ANO = C.ANO
CREATE (T)-[:Possess]->(C);

// Participante - CursoEletronico
MATCH (C:CursoEletronico), (P:Participante) WHERE C.CODCURSO = P.CODCURSO
CREATE (P)-[:Participate]->(C);

// Participante - Vinculo
MATCH (P:Participante), (V:VinculoUsp) WHERE P.NROUSP = V.NROUSP
CREATE (P)-[:Has]->(V);

// CursoEletronico - Grupo
MATCH (C:CursoEletronico), (G:Grupo) WHERE C.CODCURSO = G.CODCURSO
CREATE (C)-[:Has]->(G);


//Sequencia cria relacionamento compoegrupo a partir da tabela compoegrupo
// Grupo - CompoeGrupo
MATCH (G:Grupo), (CG:CompoeGrupo) WHERE G.NROGRUPO =  CG.NROGRUPO AND G.CODCURSO = CG.CODCURSO
CREATE (G)-[:ItsComposed]->(CG);

// CompoeGrupo - Participante
MATCH (CG:CompoeGrupo), (P:Participante) WHERE CG.NROUSP = P.NROUSP AND P.CODCURSO = CG.CODCURSO
CREATE (P)-[:Composes]->(CG);

MATCH (G:Grupo)-[:ItsComposed]->(CG:CompoeGrupo)<-[:Composes]-(P:Participante)
CREATE (P)-[:MemberOf]->(G);


// Removing Leciona
MATCH (CG:CompoeGrupo) DETACH DELETE CG;


//Sequencia cria relacionamento Teaches a partir da tabela Leciona
// Professor - Disciplina (leciona)
MATCH (L:Leciona), (P:Professor) WHERE L.NROUSPPROF = P.NROUSP
CREATE (P)-[:Leciona]->(L);

MATCH (L:Leciona), (D:Disciplina) WHERE D.COD = L.CODDISC
CREATE (L)-[:Leciona]->(D);

MATCH (P:Professor)-[:Leciona]->(L:Leciona)-[:Leciona]->(D:Disciplina)
CREATE (P)-[:Teaches]->(D);

// Removing Leciona
MATCH (L:Leciona) DETACH DELETE L;