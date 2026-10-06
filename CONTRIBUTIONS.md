# Mes contributions — SplitUL

J’ai principalement travaillé sur les calculs de répartition et sur leur exposition dans l’API.

- J’ai implémenté un algorithme glouton qui transforme les soldes nets en dettes entre paires de membres.
- J’ai ajouté l’historique des dépenses d’un groupe, avec son tri, son DTO et ses contrôles d’appartenance.
- J’ai centralisé les contrôles d’accès reposant sur l’en-tête de membre et corrigé le comportement de suppression des groupes.
- J’ai développé des stratégies de partage, leur validation et les tests associés.
- J’ai intégré OWASP Dependency-Check dans Maven et le workflow d’intégration continue du projet.

## Repères dans le code

- [src/main/java/ca/ulaval/glo2003/domain/GroupConverter.java](src/main/java/ca/ulaval/glo2003/domain/GroupConverter.java)
- [src/main/java/ca/ulaval/glo2003/domain/split/CustomSplitStrategy.java](src/main/java/ca/ulaval/glo2003/domain/split/CustomSplitStrategy.java)

## Références de mon travail

Je conserve ci-dessous les références de mes contributions. Elles renvoient aux dépôts de cours ; leur consultation peut demander un accès. Les liens vers les fichiers ci-dessus sont accessibles dans ce dépôt public.

- [PR #36](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/36)
- [PR #59](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/59)
- [PR #60](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/60)
- [PR #81](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/81)
- [PR #84](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/84)

## Mon équipe

J’ai réalisé ce projet avec Victoria Pelletier Cantin, Yan Tremblay, Dania Mahfoud et Alissa Audet. Les fonctionnalités présentées résultent de notre travail collectif dans le cadre du cours GLO-2003.

Je conserve la [licence MIT du projet](LICENSE) et les mentions d’auteur d’origine.
