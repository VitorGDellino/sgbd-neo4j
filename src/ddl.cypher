// Creating constraints
CREATE CONSTRAINT ON (V:VinculoUsp) ASSERT V.NROUSP IS UNIQUE
CREATE CONSTRAINT ON (A:Aluno) ASSERT A.NROUSP IS UNIQUE
CREATE CONSTRAINT ON (F:Funcionario) ASSERT F.NROUSP IS UNIQUE
CREATE CONSTRAINT ON (P:Professor) ASSERT P.NROUSP IS UNIQUE
CREATE CONSTRAINT ON (D:Disciplina) ASSERT D.Disciplina IS UNIQUE
CREATE CONSTRAINT ON (T:Turma) ASSERT (T.NROTURMA, T.CODDISC, T.ANO) IS NODE KEY
CREATE CONSTRAINT ON (M:Matricula) ASSERT (M.NROUSP, M.CODDISC, M.ANO) IS NODE KEY
CREATE CONSTRAINT ON (C:CursoEletronico) ASSERT (C.CODCURSO) IS UNIQUE
CREATE CONSTRAINT ON (C:CursoEletronico) ASSERT (C.NROTURMA, C.CODDISC) IS NODE KEY
CREATE CONSTRAINT ON (P:Participante) ASSERT (P.CODCURSO, P.NROUSP) IS NODE KEY
CREATE CONSTRAINT ON (G:Grupo) ASSERT (G.NROGRUPO, G.CODCURSO) IS NODE KEY
CREATE CONSTRAINT ON (CG:CompoeGrupo) ASSERT (CG.CODCURSO, CG.NROUSP) IS NODE KEY
//*************************************************************************************************************//

// Droping constraints
DROP CONSTRAINT ON (V:VinculoUsp) ASSERT V.NROUSP IS UNIQUE
DROP CONSTRAINT ON (A:Aluno) ASSERT A.NROUSP IS UNIQUE
DROP CONSTRAINT ON (F:Funcionario) ASSERT F.NROUSP IS UNIQUE
DROP CONSTRAINT ON (P:Professor) ASSERT P.NROUSP IS UNIQUE
DROP CONSTRAINT ON (D:Disciplina) ASSERT D.Disciplina IS UNIQUE
DROP CONSTRAINT ON (T:Turma) ASSERT (T.NROTURMA, T.CODDISC, T.ANO) IS NODE KEY
DROP CONSTRAINT ON (M:Matricula) ASSERT (M.NROUSP, M.CODDISC, M.ANO) IS NODE KEY
DROP CONSTRAINT ON (C:CursoEletronico) ASSERT (C.CODCURSO) IS UNIQUE
DROP CONSTRAINT ON (C:CursoEletronico) ASSERT (C.NROTURMA, C.CODDISC) IS NODE KEY
DROP CONSTRAINT ON (P:Participante) ASSERT (P.CODCURSO, P.NROUSP) IS NODE KEY
DROP CONSTRAINT ON (G:Grupo) ASSERT (G.NROGRUPO, G.CODCURSO) IS NODE KEY
DROP CONSTRAINT ON (CG:CompoeGrupo) ASSERT (CG.CODCURSO, CG.NROUSP) IS NODE KEY

//*************************************************************************************************************//

// Creating relations //

// Aluno - VinculoUsp
MATCH (V:VinculoUsp),(A:Aluno) WHERE V.NROUSP = A.NROUSP
CREATE (A)-[:Has]->(V)

// Funcionario - VinculoUsp
MATCH (V:VinculoUsp),(F:Funcionario) WHERE V.NROUSP = F.NROUSP
CREATE (F)-[:Has]->(V)

// Professor - VinculoUsp
MATCH (V:VinculoUsp),(P:Professor) WHERE V.NROUSP = P.NROUSP
CREATE (P)-[:Has]->(V)

// Disciplina - Turma
MATCH (D:Disciplina), (T:Turma) WHERE D.COD = T.CODDISC
CREATE (D)<-[:HasClass]-(T)

// Professor - Turma
MATCH  (P:Professor), (T:Turma) WHERE P.NROUSP = T.NROUSPPROF
CREATE (P)-[:Teaches]->(T)

// Vinculo - Matricula - Turma
MATCH (V:VinculoUsp), (T:Turma), (M:Matricula) WHERE V.NROUSP = M.NROUSP AND T.CODDISC = M.CODDISC
CREATE (V)-[:Has]->(M)-[:Enrolls]->(T)

// Turma - CursoEletronico
MATCH (T:Turma), (C:CursoEletronico) WHERE T.NROTURMA = C.NROTURMA AND T.CODDISC = C.CODDISC AND T.ANO = C.ANO
CREATE (T)-[:Possess]->(C)

// Participante - CursoEletronico
MATCH (C:CursoEletronico), (P:Participante) WHERE C.CODCURSO = P.CODCURSO
CREATE (P)-[:Participate]->(C)

// Participante - Vinculo
MATCH (P:Participante), (V:VinculoUsp) WHERE P.NROUSP = V.NROUSP
CREATE (P)-[:Has]->(V)

// CursoEletronico - Grupo
MATCH (C:CursoEletronico), (G:Grupo) WHERE C.CODCURSO = G.CODCURSO
CREATE (C)-[:Has]->(G)

// Grupo - CompoeGrupo
MATCH (G:Grupo), (CG:CompoeGrupo) WHERE G.NROGRUPO =  CG.NROGRUPO AND G.CODCURSO = CG.CODCURSO
CREATE (G)-[:ItsComposed]->(CG)

// CompoeGrupo - Participante
MATCH (CG:CompoeGrupo), (P:Participante) WHERE CG.NROUSP = P.NROUSP AND P.CODCURSO = CG.CODCURSO
CREATE (P)-[:Composes]->(CG)
//*************************************************************************************************************//