test:
ifndef NGINX_TAG
	$(error NGINX_TAG is undefined)
endif
	cd tests && mvn --batch-mode test -DnginxTag="$(NGINX_TAG)"

build:
ifndef IMAGE_NAME
	$(error IMAGE_NAME is undefined)
endif
ifndef NGINX_TAG
	$(error NGINX_TAG is undefined)
endif
	docker build -t "$(IMAGE_NAME)" --build-arg NGINX_TAG="$(NGINX_TAG)" .

clean:
	rm -f Dockerfile
