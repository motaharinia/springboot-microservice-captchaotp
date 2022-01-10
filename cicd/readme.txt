108:
1.gitlab-ci.yml[build-jarfile-test] (with maven tst profile) -> (jar)
2.gitlab-ci.yml[build-dockerfile-test] (with Dockerfile-tst) -> (dockerImage)
3.gitlab-ci.yml[release-on-demo] (with deploy-tst.yml) -> (deploy dockerImage)

production:
1.gitlab-ci.yml[build-jarfile-prod] (with maven prod profile) -> (jar)
2.gitlab-ci.yml[build-dockerfile-prod] (with Dockerfile-prod) -> (dockerImage)
3.gitlab-ci.yml[release-on-prod] (with deploy-prod.yml) -> (deploy dockerImage)
