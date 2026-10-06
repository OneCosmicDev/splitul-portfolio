# SplitUL — Gestion de dépenses partagées

J’ai participé au développement de SplitUL avec l’équipe 17 du cours GLO-2003 à l’Université Laval. Nous avons construit une API pour gérer les dépenses d’un groupe et représenter ce que chaque membre doit aux autres.

**Université Laval · GLO-2003 · Hiver 2025**

**Début documenté : 23 janvier 2025** — [repères chronologiques](PROVENANCE.md#repères-chronologiques)

**Technologies : Java 21 · Jersey · Maven · JUnit · Mockito · MongoDB**

## Ce que fait le projet

SplitUL permet de créer des groupes, d’ajouter des membres, d’enregistrer des dépenses et de consulter leur historique. L’API calcule les dettes selon différentes règles de répartition, notamment un partage égal ou des pourcentages personnalisés. Le projet sépare les routes de l’API, les règles métier et la persistance, avec une implémentation en mémoire et une autre utilisant MongoDB.

## Ma contribution

J’ai principalement travaillé sur les calculs de répartition et sur leur exposition dans l’API.

- J’ai implémenté un algorithme glouton qui transforme les soldes nets en dettes entre paires de membres.
- J’ai ajouté l’historique des dépenses d’un groupe, avec son tri, son DTO et ses contrôles d’appartenance.
- J’ai centralisé les contrôles d’accès reposant sur l’en-tête de membre et corrigé le comportement de suppression des groupes.
- J’ai développé des stratégies de partage, leur validation et les tests associés.
- J’ai intégré OWASP Dependency-Check dans Maven et le workflow d’intégration continue du projet.

Je détaille les fichiers et les références de mon travail dans [CONTRIBUTIONS.md](CONTRIBUTIONS.md).

## Ce que j’ai appris

J’ai appris à traduire une règle métier en un modèle explicite et testable. Une dépense partagée ne se résume pas à une division : il faut gérer les membres concernés, les pourcentages, les soldes et les erreurs de validation. L’algorithme de répartition m’a amené à distinguer le solde net d’un membre des dettes présentées par l’API.

J’ai aussi renforcé ma compréhension de la séparation entre domaine et DTO. Le domaine porte les règles, tandis que les objets de transfert exposent les informations attendues par le client. Les tests et les outils Maven m’ont permis de vérifier ces comportements et d’intégrer des contrôles de qualité dans le travail d’équipe.

## Mon équipe

J’ai réalisé ce projet avec Victoria Pelletier Cantin, Yan Tremblay, Dania Mahfoud et Alissa Audet. Les fonctionnalités présentées résultent de notre travail collectif dans le cadre du cours GLO-2003.

Je conserve la [licence MIT du projet](LICENSE) et les mentions d’auteur d’origine.

## Lancer le projet

Prérequis : Java 21 et Maven 3.

```sh
mvn compile
mvn exec:java
```

L’API écoute par défaut sur `http://localhost:8080` et utilise un stockage en mémoire. La persistance MongoDB est optionnelle. `SENTRY_DSN` peut rester absent pour une exécution locale.

```sh
mvn test
```

Le projet conserve ses outils Maven de qualité issus du cours. Les analyses de dépendances et les tests nécessitant un service externe peuvent demander une configuration supplémentaire.

## État du projet

Les **136 tests exécutables sans Docker passent**. Les **9 tests MongoDB** nécessitent Docker et restent à revalider avec Testcontainers.
