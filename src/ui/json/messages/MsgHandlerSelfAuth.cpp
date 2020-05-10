#include "MsgHandlerSelfAuth.h"

#include "InternalActivationHandler.h"

#include <QSharedPointer>
#include <QUrlQuery>

using namespace governikus;


MsgHandlerSelfAuth::MsgHandlerSelfAuth()
	: MsgHandler(MsgType::AUTH)
{
}


MsgHandlerSelfAuth::MsgHandlerSelfAuth(const QJsonObject& pObj)
	: MsgHandlerSelfAuth()
{
	initSelfAuth();
	setVoid();
}


MsgHandlerSelfAuth::MsgHandlerSelfAuth(const QSharedPointer<SelfAuthContext>& pContext)
	: MsgHandlerSelfAuth()
{
	Q_ASSERT(pContext);

	mJsonObject[QLatin1String("result")] = ECardApiResult(pContext->getStatus()).toJson();

	QString url;
	if (pContext->getRefreshUrl().isEmpty())
	{
		const auto& token = pContext->getTcToken();
		if (!token.isNull() && pContext->getTcToken()->getCommunicationErrorAddress().isValid())
		{
			url = pContext->getTcToken()->getCommunicationErrorAddress().toString();
		}
	}
	else
	{
		url = pContext->getRefreshUrl().toString();
	}

	setValue("url", url);
}


void MsgHandlerSelfAuth::initSelfAuth()
{
	auto handler = ActivationHandler::getInstance<InternalActivationHandler>();
	Q_ASSERT(handler);
	handler->runSelfAuthentication();
}


void MsgHandlerSelfAuth::setError(const QLatin1String pError)
{
	setValue("error", pError);
}
