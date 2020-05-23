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

	QJsonObject obj;
	auto data = pContext->getSelfAuthenticationData().getOrderedSelfData();
	for (const auto &d : data)
	{
		obj[d.first] = d.second;
	}
	mJsonObject[QLatin1String("data")] = obj;
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
