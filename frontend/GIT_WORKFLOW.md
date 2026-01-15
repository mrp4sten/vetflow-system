# VetFlow Frontend - Git Workflow

## Branch Strategy

We use a simplified git workflow optimized for frontend development:

### Main Branches

1. **`master`** - Production-ready code
   - Only merge when we have a complete, tested release
   - Tagged with version numbers (v0.1.0, v0.2.0, etc.)
   - Deployed to production

2. **`frontend-development`** - Active development branch
   - All frontend development happens here
   - Continuous integration of features
   - Merge to master only for releases

## Workflow

### Daily Development

```bash
# Always work on frontend-development branch
git checkout frontend-development

# Make your changes and commit regularly
git add <files>
git commit -m "feat: description of feature"

# Push to remote regularly
git push origin frontend-development
```

### Commit Message Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add new feature
fix: bug fix
docs: documentation changes
style: code style changes (formatting, etc.)
refactor: code refactoring
test: adding or updating tests
chore: maintenance tasks
```

#### Examples:
```bash
git commit -m "feat(patients): add patient registration form"
git commit -m "fix(appointments): correct date validation"
git commit -m "docs: update API documentation"
git commit -m "chore(deps): update react to v18.2"
```

### Creating a Release

When ready to release a new version:

```bash
# Ensure frontend-development is up to date
git checkout frontend-development
git pull origin frontend-development

# Switch to master and merge
git checkout master
git pull origin master
git merge frontend-development --no-ff -m "Release v0.x.0: Description"

# Tag the release
git tag -a v0.x.0 -m "Release v0.x.0

- Feature 1
- Feature 2
- Bug fixes"

# Push everything
git push origin master
git push origin v0.x.0

# Return to development
git checkout frontend-development
```

## Branch Cleanup

We don't create feature branches for frontend. All work goes directly to `frontend-development`.

### Why This Approach?

1. **Simpler workflow** - No need to manage multiple feature branches
2. **Faster iteration** - Commit and push immediately
3. **Better collaboration** - Everyone works on the same branch
4. **Less overhead** - No need to create/merge/delete feature branches
5. **Clear history** - Linear development history on frontend-development

## Release Strategy

### Version Numbering

We use Semantic Versioning (SemVer): `MAJOR.MINOR.PATCH`

- **MAJOR** (1.0.0) - Breaking changes, major redesign
- **MINOR** (0.1.0) - New features, backwards compatible
- **PATCH** (0.0.1) - Bug fixes, small improvements

### Release Checklist

Before merging to master:

- [ ] All features are complete and tested
- [ ] No console errors in development
- [ ] Code is formatted and linted
- [ ] Documentation is updated
- [ ] DEVELOPMENT_STATUS.md is updated
- [ ] Create release notes

## Current Status

- **Latest Release**: v0.2.0
- **Development Branch**: frontend-development
- **Next Release**: v0.3.0 (planned)

## Tips

1. **Commit often** - Small, focused commits are better
2. **Write good messages** - Future you will thank you
3. **Push regularly** - Don't lose work
4. **Test before releasing** - Master should always be stable
5. **Update docs** - Keep documentation current

## Questions?

If you're unsure about the workflow, just remember:
- Work on `frontend-development`
- Commit regularly with good messages
- Push to remote daily
- Merge to `master` only for releases