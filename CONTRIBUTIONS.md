# SplitUL — contribution et crédits

SplitUL est une API de gestion des dépenses partagées, développée par l'équipe 17 du cours GLO-2003 à l'Université Laval. Elle permet de gérer des groupes, leurs membres, leurs dépenses et les dettes associées.

## Ma contribution — Juan José Castilla Manrique

J'ai contribué à la répartition des dépenses et des dettes, aux contrôles d'accès de l'API et à la vérification des dépendances.

- **Répartition des dettes entre membres** : implémentation d'un algorithme glouton convertissant les soldes nets en dettes entre paires de membres, dans la couche de conversion vers les DTO. [PR #36](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/36).
- **Historique des dépenses** : ajout de l'endpoint de consultation, de son DTO, du tri et des contrôles d'appartenance au groupe, avec des tests. [PR #59](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/59).
- **Filtrage des accès** : centralisation des contrôles utilisant l'en-tête de membre et correction de la suppression des groupes, avec adaptation des tests. [PR #60](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/60).
- **Stratégies de partage** : implémentation de stratégies de répartition, de pourcentages personnalisés et de leur validation, avec les tests associés. [PR #81](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/81).
- **Analyse des dépendances** : intégration d'OWASP Dependency-Check dans Maven et le workflow CI. [PR #84](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/84).

## Équipe et crédits

L'équipe indiquée dans le README source réunit Victoria Pelletier Cantin, Yan Tremblay, Dania Mahfoud, Alissa Audet et Juan José Castilla Manrique.

Comptes contributeurs identifiés : [VictoriaaPc](https://github.com/VictoriaaPc), [Yurhuval](https://github.com/Yurhuval), [mhfdania](https://github.com/mhfdania), [AlissaAudet](https://github.com/AlissaAudet) et [OneCosmicDev](https://github.com/OneCosmicDev).

Le projet s'inscrit dans le cadre pédagogique du cours GLO-2003. Le fichier LICENSE et les mentions d'origine du dépôt doivent être conservés lors de sa redistribution. Les contributions décrites ci-dessus ne m'attribuent pas l'ensemble de l'API ni les contributions des autres membres.
